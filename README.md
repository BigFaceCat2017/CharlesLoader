# CharlesLoader
Charles v4.x 动态启动loader

###### 2017-06-19
* 更新loader，详见TestCharles文件夹内，已经编译好一份jar包，将该jar包放在charles程序根目录下，并修改charles.ini，添加vmarg.5=-javaagent:charlesLoader.jar  ，其中vmarg.x根据实际情况进行修改，可参考文件夹内的示例charles.ini文件

* loader源码已开源

* 没有测试mac和linux版本，应当支持

##### 2017-06-20
* 发现mac下并不存在类似windows的charles配置文件可以添加javaagent变量，当然可以设置系统的，为减少麻烦，更新了下TestCharles下的jar包，可直接使用java -jar命令对charles.jar包进行直接破解，破解完成后替换回原包即可

##### 2017-07-18
* 更新查找待破解类的方式，准确性提升，修复识别错误

![Markdown](http://i1.buimg.com/1949/1738a2ef10c8a0d9.png)

该方法需要两个输入变量，1是原charles.jar包路径，2是新jar包的路径
