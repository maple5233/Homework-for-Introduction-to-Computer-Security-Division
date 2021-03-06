## ElGamal密码体系 

### 概述

+ ElGamal是一种基于**离散对数**的公钥密码。
+ 与DH密钥分配体制密切相关。
+ 用于数字签名标准DSS和电邮标准。

### 算法

#### 生成密钥

用户A，B共同选择素数q，α是q的原根（若g mod p的某个乘方等于$φ(p)$，则称g为模p的一个原根。$φ(p)$表示p的欧拉函数）

对于用户A

1. 随机生成整数$X_A$ 使得$1<X_A<q-1$
2. 计算$Y_A = a^{X_A} \ mod \ q$
3. A的私钥为$X_A$，公钥为$\{q,α,Y_A\}$

对于其他用户B

1. 将消息表示为整数M，其中$1<M<q-1$ 以分组密码序列方式发送消息，每个消息块长度大于q
2. 选择任意数k 使得$1≤k≤q-1$  注意：原则上这里每条信息都要新选择一个k以增强强度，K称为临时密钥
3. 计算密钥$K = (Y_A)^{k} \ mod \ q$
4. 将M加密为密文对$$C_1 = a^k \ mod \ q  \ ; \ C_2 = KM  \ mod  \ q$$


用于A解密

1. 计算$$K = (C_1)^{X_A} \ mod \ q$$
2. 计算$M= (C_2K^{-1}) \  mod  \ q$

### 安全性

+ ElGamal签名的安全性依赖于乘法群$(IFp)^*$ 上的离散对数计算。素数p必须足够大，且p-1至少包含一个大素数
+ M一般都应采用信息的HASH值(如SHA算法)。ElGamal的安全性主要依赖于p和α，若选取不当则签名容易伪造，应保证α对于p-1的大素数因子不可约。
+ 惧怕**选择密文攻击**

### 实际使用

+ 和RSA一样一般用来加密对称密码的密钥
+ 可以用于数字签名