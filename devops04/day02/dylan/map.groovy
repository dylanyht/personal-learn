versions = [
	"jdk": "1.8.0",
	"maven" : "3.5.0",
	"jenkins" : "2.332.0"
]
//根据key获取value
println(versions["jdk"])
//根据key重新赋值
versions["jdk"] = "1.11.0"
println(versions)

versions.jenkins = "2.333.0"
println(versions)

//获取key的value
println(versions.get("jdk"))
//返回map的key列表
println(versions.keySet())
//根据key删除元素
versions.remove("maven")
println(versions)

//判断map是否包含某个key或value
println(versions.containsKey("jenkins"))
println(versions.containsValue("/usr/local/gradle"))