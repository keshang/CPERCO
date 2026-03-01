# CPERCO

CPERCO 是一个基于 **Java + IBM CPLEX + R(Rserve)** 的鲁棒优化实验仓库，包含生产计划与定向越野（Orienteering）等模型代码，以及对应的数据与结果文件。

## 仓库内容

- `src/productionplanning/`：生产计划相关模型与实验入口。
- `src/orienteering/`：定向越野问题模型、数据读取与实验入口。
- `src/util/ConnectR.java`：Java 与 Rserve 的连接与数据传输。
- `data/`：实验输入数据。
- `results/`：实验输出结果（参数、目标值与概率指标等）。

## 运行前依赖

- JDK 8
- IBM CPLEX（Java API）
- R + Rserve
- REngine / RserveEngine 相关 Jar

## 快速说明

1. 先确保 CPLEX 与 Rserve 可用。
2. 根据本机环境调整代码中的路径配置（例如结果输出路径）。
3. 运行 `src/eprodemo/main.java`、`src/orienteering/RobustOpt.java` 或 `src/productionplanning/RobustPP.java` 进行实验。
