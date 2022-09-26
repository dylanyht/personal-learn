/*
如果println(a,b)失败（肯定失败，因为有语法错误）
catch捕获错误，并打印错误。
finally 总是执行。
*/

try {
	println(a,b)
}
catch(Exception e) {
	println(e)
}
finally {
	println("done")
}