/*
定义参数branchName
匹配 develop  则打印develop ，跳出。
匹配 release  则打印release ，跳出。
默认匹配， 打印 error ，退出。


*/

String branchName = "release1"

switch(branchName){
    case "develop":
        println("develop......")
        break
    case "release":
       println("release")
       break 
    default:
    println("error")
}