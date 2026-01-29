# Studying the energy consumption of Web-based Geographic Information Systems

## Abstract
Web-based Geographic Information Systems (GIS) enable the visualization and interaction with spatial data directly through a web browser. These applications can be implemented using different client–server architectures, based on raster or vector data models, each with distinct implications for processing and data transmission. Beyond functional performance, the growing environmental impact of the information technology sector positions energy efficiency as a key criterion in the design of sustainable web-based GIS.
This article examines the relationship between functionality and energy consumption when performing map request and visualization operations across different web-based GIS architectures. To this end, three experiments are conducted at three zoom levels (medium, close, and really close) selected to represent specific use cases: an overview of a large area, an intermediate view, and a detailed close-up. In each experiment, five representative web-based GIS with different client–server architectures are compared simulating typical user interactions such as panning, zooming, and route navigation at varying levels of detail: a fully client-side, a server-driven on-demand approach, a server-assisted on-demand approach, a server-assisted on-demand delegating data serialization and preparation to the DBMS; and an hybrid approach.
In conclusion, at the medium zoom level, full server-side rendering provides the best balance in terms of energy efficiency and response time, clearly outperforming client-side vector-based approaches; at the close zoom level, server-side rendering remains the most suitable solution despite reduced interactivity; and at the very close zoom level, the hybrid strategy offers a practical compromise, enabling client-side interaction on selected layers while maintaining competitive energy efficiency.
## What is this?

This repository contains all the information used for this study: the measured software and the analysis files for each experiment.
The repository also includes the resulting empirical results and some samples of the energy records obtained from the hardware measurement instrument used in the study.


## How is structured?

This folder contains three main folders: code, empirical results and sample logs.

## Code Folder
This folder contains the software under test. It consists of a web application that has a single codebase and five possible deployment configurations. These configurations are set up to be launched simultaneously or individually, depending on the needs of the production or development environment. More details about the code structure and deployment instructions can be found in the `code/README.md` file.

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
This directory contains three CSV files, one for each experiment. Each file contains the measurement data for each test case. 
