/*
定义变量参数branchName
如果branchName 等于dev则打印dev，
如果branchName 等于test则打印test，
上面都不匹配则打印skipdeploy
*/

String branchName = "test"
if ( branchName == "dev" ){
    println("dev...")
}else if ( branchName == "test" ){
    println("test....")
}else {
    println("skipdeploy....")
}