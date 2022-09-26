tools = ["jenkins","gitlab","maven","sonarqube"]

for(tool in tools) {
	println(tool)
}

tools.size().times { i ->
	println(i)
}