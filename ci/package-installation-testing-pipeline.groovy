library("ff-pipeline-lib@1.4.0")

pipeline {
	agent { label 'standard-current' }

	parameters {
		string(name: 'BUILD_PACKAGE_ID', defaultValue: '', description: 'The Build Package ID to install')
	}
	
	environment {
		ORG_USERNAME = ''
	}

	stages {
		stage('Create Org without Namespace') {
			steps {
				script{
					ORG_USERNAME = ff.createOrg("-n -f config/project-scratch-def-CI.json -w 30 -d 1")
				}
			}
		}
		stage('Installation Testing') {
			steps {
				script{
					sh """
						sfdx force:package:install -p ${params.BUILD_PACKAGE_ID} -s AdminsOnly -u ${ORG_USERNAME} -w 90 -b 30
					"""
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
