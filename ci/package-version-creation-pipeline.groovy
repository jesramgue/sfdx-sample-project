library("ff-pipeline-lib@1.4.0")

pipeline {
	agent { label 'standard-current' }

	environment {
		BUILD_PACKAGE_VERSION_ID = ''
	}

	parameters {
		booleanParam(name: 'CALCULATE_CODE_COVERAGE', defaultValue: false, description: 'Calculates code coverage when building the package version')
	}

	stages {
		stage('Init') {
			steps {
				withAnt {
					sh "ant clean init"
				}
			}
		}
		stage('Build Package Version'){
			steps{
				script {
					String packageName = "SFDX Sample Project"
					Number waitTime = 60
					BUILD_PACKAGE_VERSION_ID = ff2gp.buildPackageVersion(packageName, waitTime, params.CALCULATE_CODE_COVERAGE)
				}
			}
		}
		stage('Tag Repo'){
			steps{
				script {
					ff2gp.tagRepoWithVersionNumber(BUILD_PACKAGE_VERSION_ID)
				}
			}
		}
		stage('Kick off Installation Testing'){
			steps{
				script {
					build job: "sample-project-package-installation-testing-pipeline", parameters: [
						string(name: 'BRANCH', value: env.GIT_BRANCH),
						string(name: 'BUILD_PACKAGE_ID', value: BUILD_PACKAGE_VERSION_ID),
					], wait: false
				}
			}
		}
	}

	post {
		always {
			cleanWs()
		}
	}
}
