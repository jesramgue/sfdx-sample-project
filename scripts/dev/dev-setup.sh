#!/bin/bash
ant init
sfdx force:org:create -f config/project-scratch-def-DEV.json -d 1 -a sample-project-test-org -s
sfdx force:source:push