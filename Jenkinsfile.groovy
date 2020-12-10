pipeline {
	agent { label 'standard-current' }
	
	environment {
        // Shared parameter for all the stages
		ORG_USERNAME = ''
	}

	options {
		// I know my pipeline should take 5 min. If toke 15 maybe is in a loop. This will cancel the run.
		timeout(time: 15, unit: 'MINUTES')
	}
 
	stages {
		stage('Build & Test'){
			parallel{
				// Parallel 1
				stage('Apex'){
					stages{
						stage('Initialize'){
							steps{
								// Most of out products need an initization step for getting dependencies, prepare files/folders, ...
								sh "ant clean init"
							}
						}
						stage('Create Org'){
							steps{
								script{
									// sh with flag "returnStdout: true", allow us to manage the result of whatever you have ran.
									ORG_USERNAME = sh (script: """
											sfdx force:org:create -f config/project-scratch-def-CI.json -w 30 -d 1 --json | jq .result.username -r
										""", returnStdout: true).trim()
									if (ORG_USERNAME == '') {
										error "Unable to create Scratch Org"
									}
								}
								echo "Org UserName: ${ORG_USERNAME}"
							}
						}
						stage('Push To Org'){
							steps{
								sh "sfdx force:source:push -f -u ${ORG_USERNAME}"
							}
						}
						stage('Run Tests'){
							steps{
								// The results are generated in a file named: junit.xml
								sh "sfdx force:apex:test:run -n ControllerTest,AnotherControllerTest -u ${ORG_USERNAME} -w 15 -d test-results/apex --json"
								// The previous way of running tests is mainly for learning purposes. We highly recommend to use our internal tool as follow below.
								// sh "sfdx ff:test:testall -m 2 -u ${ORG_USERNAME}"
							}
						}
					}
				}
				// Parallel 2 
				stage('NonApex'){
					// This stage will be run in a different instance that the previous one (have different agent)
					agent { label 'siesta-blueocean' }
					stages{
						stage('Initialize'){
							steps{
								// This step is needed again because in this instance we haven't done the initization
								sh "ant clean init"
							}
						}
						stage('Show Info'){
							steps{
								script{
                                    // These parameters will be only available in this stage
									BRANCH_NAME = sh (script: "echo \"${GIT_LOCAL_BRANCH}\" | sed -e 's/\\//-/g'", returnStdout: true).trim()
									TMP_FILE = "myInfoFile.txt"
								}
								echo "Run: \"/${BRANCH_NAME}_${BUILD_NUMBER}\", using commit ${GIT_COMMIT}"
							}
						}
					}
				}

			}
		}
	}
	post {
		always {
			// Save unit tests results to be shown in the "Tests Tab" 
			junit testResults: 'test-results/apex/*-junit.xml', allowEmptyResults: true
			// Will save any file in this folder so I can have access to it when the run has finished 
			archiveArtifacts artifacts: 'test-results/**', allowEmptyArchive: true
			// !! Saving files at this point, will take them ONLY from the main agent (standard-current).
			// !!   If you want to save files from a diferent agent, you should add these steps inside the section using that label

			script {
				if (ORG_USERNAME) {
					// Always delete the orgs created on CI so not the exceed the limits
					sh "sfdx force:org:delete -p -u ${ORG_USERNAME}"
				}
			}
		}
		failure {
			sendSlackMessage("Failure", "danger")
		}
		unstable {
			sendSlackMessage("Unstable (failing tests)", "warning")
		}
		fixed {
			sendSlackMessage("Back to normal", "good")
		}

	}
}

// ===================================================================
// We can create and use method to re-use code and make more readable
// ===================================================================

void sendSlackMessage(String message, String color) {
	script {
		if (env.GIT_BRANCH ==~ /(develop|release\/.*)/) {
			def projectName = env.JOB_NAME.tokenize('/')[1].replaceAll('%2F', '/')
			slackSend(
				channel: "#rnd-grads-interns2018",
				color: "${color}",
				message: "<${env.RUN_DISPLAY_URL}|'$projectName [${env.BUILD_NUMBER}]'> ${message}"
			)
		}
	}
}

