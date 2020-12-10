#!/bin/bash
orgUsername=$1

# Params
# $1 - The exit code of the previous process
handleError() {
	if [ $1 -ne 0 ]
	then
		return 1
	fi
}

cp scripts/org-pooling/multi-stage-push-project-templates/sfdx-project-force-app.json sfdx-project.json
sfdx force:source:push -f -u ${orgUsername}
handleError $?

cp scripts/org-pooling/multi-stage-push-project-templates/sfdx-project-force-app-autotests.json sfdx-project.json
sfdx force:source:push -f -u ${orgUsername}
handleError $?