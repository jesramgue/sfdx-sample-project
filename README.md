# SFDX Sample Project

This SFDX Sample Project demonstrates FinancialForce's preferred SFDX project structure. It is a guide and isn't intended to be prescriptive.

## Structure
This structure is intended to closely match the default SFDX project structure. You may require other product specific directories at the root level - this example structure isn’t intended to be exhaustive.

The structure in `force-app` is intended to roughly match other build systems by splitting out unit tests into their own test directory. Other, non packagable test sources are split into their own separate `force-app` (e.g. `force-app-autotests`) to suit second generation packaging.

* `autotests`
  * Contains all non deployable sources for automated tests
  * Split down by test type; integration, ui etc
* `config`
  * Contains scratch org definitions
* `data`
  * Contains data files for data loading into the org, which could be organised into sub-directories if required
* `force-app` 
  * Is the “default” path in the `sfdx-project.json` packageDirectories
  * Contains packagable sources only (to align ourselves with Second Generation Packaging)
  * `deps` Is gitignored and contains all the projects dependencies, such as fflib
  * `generated` Is gitignored and contains all generated sources, e.g. Sencha apps, mocks files etc
  * `main` Contains the main, tracked sources for this project. Sub-divided by additional directories for grouping related features / functionality (i.e. into Java like packages)
  * `tests` Contains the unit tests which are organised in directories that match the product sources
* `force-app-autotests`
  * Is not set as “default” path in the `sfdx-project.json` packageDirectories
  * This contains the deployable sources for automated tests
  * It is split down in the same way as autotests directory, i.e. by test type
* `scripts`
  * This directory contains useful scripts that automate certain repetitive tasks, such as initial setup for development

The `JenkinsFile` in this repo is intended to be a sample and won't execute without some modifications.

