
// //定义一个字符串类型的变量
// String name = "lisi"

// println(name)

// //定义一个变量包含多行内容
// String zeyang = """
//   devops
// """
// println(zeyang)

//字符串分割操作
String branchName = "release-1.1.1"
// println(branchName.split("-"))
// println(branchName.split("-")[1])
// println("${env.JOB_NAME}".split("-")[0])

// //是否包含某字符串
// println(branchName.contains("release"))

// //字符串的长度
// println(branchName.size())
// println(branchName.length())

// //使用变量作为值
// def message = "hello ${name}"
// println(message)
// println(message.toString())

// //获取元素索引值
// println(branchName.indexOf("-"))

// //判断字符串以DEV结尾
// String jobName = "test-service-DEV"
// println(jobName.endsWith("DEV"))

// //字符串增减操作哦
// String log = "error: xxxxxxx aa"
// println(log.minus("a"))
// println(log - "a")
// println(log.plus("aa"))
// println(log + "bbb")

// //字符串反转
// String nums = "1234567"
// println(nums.reverse())

