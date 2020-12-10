pipeline {
	agent { label 'standard-current' }
	
	environment {
        // Shared parameter for all the stages
		ORG_USERNAME = ''

        // Constant parameters easy to locate and change
		APPIRIO_PACKAGE_ID = '04t1E000000ckby'
		ERP_PACKAGE_ID = '04t2X000000AeuM'

		// NEVER hardcode any password. Use "credentials". Remember this is in a Cloud Server and could be hacked!
		ERP_PACKAGE_PASSWORD = credentials('ERP_FALL_2019_INTERNAL_PASS')
	}

	options {
		// I know my pipeline should take 10 min. If toke 30 maybe is in a loop. This will cancel the run.
		timeout(time: 20, unit: 'MINUTES')
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
						stage('Install Appirio'){
							steps{
                                echo "Installing Package"
								sh "sfdx force:package:install -u ${ORG_USERNAME} -p ${APPIRIO_PACKAGE_ID} -w 60 -r"
							}
						}
                        stage('Install ERP'){
							steps{
                                echo "Installing Package that need a password"
								sh "sfdx force:package:install -u ${ORG_USERNAME} -p ${ERP_PACKAGE_ID} -k ${ERP_PACKAGE_PASSWORD} -w 60 -r"
							}
						}
						stage('Push To Org'){
							steps{
								sh "sfdx force:source:push -f -u ${ORG_USERNAME}"
							}
						}
						stage('Run Tests'){
							steps{
								// By default, the results are generated in a file named: test-result-testall
								sh "sfdx ff:test:testall -m 2 -u ${ORG_USERNAME}"
							}
						}
					}
				}
				// Parallel 2 
				stage('Siesta Tests'){
					// This stage will be run in a different instance that the previous one (have different agent)
					agent { label 'siesta-blueocean' }
					stages{
						stage('Initialize'){
							steps{
								// This step is needed again because in this instance we haven't done the initization
								sh "ant clean init"
							}
						}
						stage('Setup Siesta'){
				            steps{
				                script{
				                    BRANCH_NAME = sh (script: "echo \"${GIT_LOCAL_BRANCH}\" | sed -e 's/\\//-/g'", returnStdout: true).trim()
				                    CONF_FILE = sh (script: "echo \"/etc/httpd/conf.d/${BRANCH_NAME}_${BUILD_NUMBER}.conf\"", returnStdout: true).trim()
				                    TMP_FILE = sh (script: "echo \"/tmp/${BRANCH_NAME}_${BUILD_NUMBER}.conf\"", returnStdout: true).trim()
				                }
				                sh """
				                    rm -f ${TMP_FILE}
				                    echo "Alias \"/${BRANCH_NAME}_${BUILD_NUMBER}\" ${WORKSPACE}" > ${TMP_FILE}
				                    sudo mv ${TMP_FILE} ${CONF_FILE}
				                    sudo /sbin/service httpd restart
				                """
				            }
				        }
						stage('Run Siesta Planner Tests'){
		                    steps{
		                        retry(2) {
		                            withAnt{
		                                sh """
		                                    ant buildApp -buildfile sencha/Planner/build.xml -Dunit.tests.run=true -Dunit.tests.base.url=http://localhost:60000/${BRANCH_NAME}_${BUILD_NUMBER}/sencha/Planner/resource -Dunit.tests.filter=unit
		                                """
		                            }
		                        }
		                    }
		                }
					}
				}

			}
		}
	}
	post {
		always {
			// Save unit tests results so to be shown in the "Tests Tab" 
			junit testResults: 'test-result-testall.xml', allowEmptyResults: true
			// Will save any file in this folder so I can have access to it when the run has finished 
			archiveArtifacts artifacts: 'test-results/**', allowEmptyArchive: true
			// !! Saving files at this point, will take them ONLY from the main agent (standard-current).
			// !!   If you want to save files from a diferent agent, you should add these steps inside the section using that label
		}
		success {
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

// ==============================================================
// We can create and use method to re-use code of more readable
// ==============================================================

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
