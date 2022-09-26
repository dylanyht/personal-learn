String name = 'zeyang'
name = 'jenkins'

fullName = "li ${name}"
println(fullName)

text = """
a=1,
b=2,
c=3
${name}
"""

println(text)

// 字符串操作
// 业务名称-应用名称-类型_环境
jobName = "devops-t1-service_DEV"
//第一个 0 
buName = jobName.split("-")[0]   //list ["devops", "t1","service_DEV"]
println(buName)

//分支名称
branchName = "release-1.1.1"
containsRelease = branchName.contains("release")
println(containsRelease)

strs = branchName.split("-")
println(strs.size())
println(strs[0].length())

//indexOf
str = "release-1.1.2"
jobName = "app-service-dev"
println(str.indexOf("-"))
println(jobName.endsWith("dev"))
println(jobName - "dev")
println(jobName + "app")








