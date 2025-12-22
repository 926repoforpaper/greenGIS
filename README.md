# Title

[Miguel R. Luaces](https://orcid.org/0000-0003-0549-2000)

[Alberto Gordillo](https://orcid.org/0000-0002-4742-173X)  

[Félix García](https://orcid.org/0000-0001-6460-0353)

[Mª Ángeles Moraga](https://orcid.org/0000-0001-9165-7144)

[Coral Calero](https://orcid.org/0000-0003-0728-4176)


## Abstract

## What is this?

This repository contains all the information used for this study: the measured software and the analysis files for each experiment.
The repository also includes the resulting empirical results and some samples of the energy records obtained from the hardware measurement instrument used in the study.


## How is structured?

This folder contains three main folders: code, empirical results and sample logs.

## Code Folder
This folder contains the software under test. It consists of a web application that has a single codebase and five possible deployment configurations. These configurations are set up to be launched simultaneously or individually, depending on the needs of the production or development environment.

## Empirical Results Folder
This folder contains three folders, one for each experiment
```
.
├── Experiment1/          # Folder containing all the information from the analysis of Experiment 1
│   ├── Gismedium/        # Folder containing all the information processed with ELLIOT
│   │   ├── img/          # This folder contains the graphs of the execution of each measurement and the box-plots.
│   │   └── report/       # This folder contains Excel files with descriptive statistics for each measurement, test case run and product.
│   └── SPSS/             # Files with statistics generated using SPSS
│
├── Experiment2/          # Folder containing all the information from the analysis of Experiment 2
│   ├── Gisclose/         # Folder containing all the information processed with ELLIOT
│   │   ├── img/          # This folder contains the graphs of the execution of each measurement and the box-plots.
│   │   └── report/       # This folder contains Excel files with descriptive statistics for each measurement, test case run and product.
│   └── SPSS/             # Files with statistics generated using SPSS
│
└── Experiment3/          # Folder containing all the information from the analysis of Experiment 3
    ├── Gisreallyclose/   # Folder containing all the information processed with ELLIOT
    │   ├── img/          # This folder contains the graphs of the execution of each measurement and the box-plots.
    │   └── report/       # This folder contains Excel files with descriptive statistics for each measurement, test case run and product.
    └── SPSS/             # Files with statistics generated using SPSS

```

## Sample Logs Folder

## Contacts and References

[Green Team Alarcos](https://greenteamalarcos.uclm.es/)
