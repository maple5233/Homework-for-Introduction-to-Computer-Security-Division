[TOC]

# RSA

## 概述

+ RSA加密算法是一种非对称加密算法。
+ 对极大整数做因数分解的难度决定了RSA算法的可靠性。换言之，对一极大整数做因数分解愈困难，RSA算法愈可靠。
+ 到目前为止，世界上还没有任何可靠的攻击RSA算法的方式。只要其钥匙的长度足够长，用RSA加密的信息实际上是不能被解破的。

## 加密流程

### 公钥与私钥的产生

1. 随意选择两个大的质数${\displaystyle p}$和${\displaystyle q}$，${\displaystyle p}$不等于${\displaystyle q}$，计算${\displaystyle N=pq}$。
2. 根据[欧拉函数](https://zh.wikipedia.org/wiki/%E6%AC%A7%E6%8B%89%E5%87%BD%E6%95%B0)，求得${\displaystyle r=\varphi (N)=\varphi (p)\varphi (q)=(p-1)(q-1)}$
3. 选择一个小于${\displaystyle r}$的整数${\displaystyle e}$，使${\displaystyle e}$与${\displaystyle r}$互质。并求得${\displaystyle e}$关于${\displaystyle r}$的**模逆元**，命名为${\displaystyle d}$    也就是求${\displaystyle d}$使得${\displaystyle ed\equiv 1{\pmod {r}}}$。(模逆元存在，当且仅当${\displaystyle e}$与${\displaystyle r}$互质）
4. 将质数${\displaystyle p}$和${\displaystyle q}$的记录销毁。
5. $(N,e)$是公钥，${\displaystyle (N,d)}$是私钥。

### 加密消息

假设Bob想给Alice送一个消息${\displaystyle m}$，他知道Alice产生的${\displaystyle N}$和${\displaystyle e}$。

1. 他使用起先与Alice约好的格式将${\displaystyle m}$转换为一个小于${\displaystyle N}$，且与${\displaystyle N}$互质的整数${\displaystyle n}$，比如他可以将每一个字转换为这个字的Unicode码，然后将这些数字连在一起组成一个数字。假如他的信息非常长的话，他可以将这个信息分为几段，然后将每一段转换为${\displaystyle n}$。
2. 用这个公式他可以将${\displaystyle n}$加密为${\displaystyle c}$： $$n^e \equiv c\ (\mathrm{mod}\ N) $$
3. Bob算出${\displaystyle c}$后就可以将它传递给Alice。

### 解密消息

Alice得到Bob的消息$c$后就可以利用她的密钥$d$来解码。

1. 她可以用以下这个公式来将$c$转换为$n$：

   $$ c^d \equiv n\ (\mathrm{mod}\ N) $$

2. 得到$n$后，她可以将原来的信息$m$重新复原。

3. 解码的原理是

   $$c^d \equiv n^{e \cdot d}\ (\mathrm{mod}\ N)$$

   已知${\displaystyle ed\equiv 1{\pmod {r}}}$，即$ {\displaystyle ed=1+h\varphi (N)}$。 由[欧拉定理](https://zh.wikipedia.org/wiki/%E6%AC%A7%E6%8B%89%E5%AE%9A%E7%90%86_(%E6%95%B0%E8%AE%BA))得：
   $$
   {\displaystyle n{ed}=n{1+h\varphi (N)}=n\left(n^{\varphi (N)}\right)^{h}\equiv n(1)^{h}{\pmod {N}}\equiv n{\pmod {N}}}
   $$



## 签名消息

RSA也可以用来为一个消息署名。假如Alice想给Bob传递一个署名的消息的话，那么她可以：

1. 为她的消息计算一个散列值
2. 然后用她的私钥加密这个散列值并将这个“署名”加在消息的后面。这个消息只有用她的公钥才能被解密。
3. Bob获得这个消息后可以用Alice的公钥解密这个散列值，然后将这个数据与他自己为这个消息计算的散列值相比较。
4. 假如两者相符的话，那么他就可以知道发信人持有Alice的密钥，以及这个消息在传播路径上没有被篡改过。

## 细节

### 密钥生成

+ 要使用概率算法来验证随机产生的大的整数是否质数
+ 假如有一个数通过了这个测试的话，那么要使用一个精确的测试来保证它的确是一个质数
+ 除此之外这样找到的$p$和$q$还要满足一定的要求：
  + 它们不能太靠近
  + 此外$p-1$或$q-1$的因子不能太小
  + 否则的话$N$也可以被很快地分解
+ 此外寻找质数的算法不能给攻击者任何信息，这些质数是怎样找到的，尤其产生随机数的软件必须非常好。要求是随机**和**不可预测。
+ 此外密钥$d$必须足够大，1990年有人证明假如$p$大于$q$而小于$2q$(这是一个很经常的情况）而${\displaystyle d<{\frac {1}{3}}\times N^{\frac {1}{4}}}$，那么从$N$和$e$可以很有效地推算出$d$。此外$e=2$永远不应该被使用。

### 时间攻击

1995年有人提出了一种非常意想不到的攻击方式：假如Eve对Alice的硬件有充分的了解，而且知道它对一些特定的消息加密时所需要的时间的话，那么她可以很快地推导出*d*。这种攻击方式之所以会成立，主要是因为在进行加密时所进行的模指数运算是一个比特一个比特进行的，而比特为1所花的运算比比特为0的运算要多很多，因此若能得到多组消息与其加密时间，就会有机会可以反推出私钥的内容。

### 速度

比起DES和其它对称算法来说，RSA要慢得多。实际上Bob一般使用一种对称算法来加密他的信息，然后用RSA来加密他的比较短的对称密码，然后将用RSA加密的对称密码和用对称算法加密的消息送给Alice。

### 密钥分配与中间人攻击

+ 和其它加密过程一样，对RSA来说分配公钥的过程是非常重要的。
+ **分配公钥的过程必须能够抵挡中间人攻击**。
+ 假设Eve交给Bob一个公钥，并使Bob相信这是Alice的公钥，并且她可以截下Alice和Bob之间的信息传递，那么她可以将她自己的公钥传给Bob，Bob以为这是Alice的公钥。Eve可以将所有Bob传递给Alice的消息截下来，将这个消息用她自己的密钥解密，读这个消息，然后将这个消息再用Alice的公钥加密后传给Alice。
+ 理论上Alice和Bob都不会发现Eve在偷听他们的消息。
+ 今天人们一般用可靠的第三方机构签发证书来防止这样的攻击。

### 典型密钥长度

用户应使用1024位密钥，证书认证机构应用2048位或以上。