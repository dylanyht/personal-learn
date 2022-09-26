def mylist = [1,2,3,4,4,"devops"]
// println(mylist)

// //list的元素增删
// println(mylist + "jenkins")
// println(mylist - "devops")
// println(mylist << "java")

// def newlist = mylist + "ceshi"
// println(newlist)

//判断元素是否为空
println(mylist.isEmpty())

//列表去重
println(mylist.unique())
//列表反转
println(mylist.reverse())
//列表排序
println(mylist.sort())

//判断列表是否包含元素
println(mylist.contains("devops"))

//列表的长度
println(mylist.size())

//扩展列表定义方式
String[] stus = ["zhangsan","lisi","wangwu"]
def numlist = [1,2,3,4,4,4] as int[]

//通过索引获取列表元素
println(numlist[2])

//计算列表中元素出现的次数
println(numlist.count(4))