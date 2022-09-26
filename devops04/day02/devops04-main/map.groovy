
versions = [
	"jdk": "1.8.0",
	"maven" : "3.5.0",
	"jenkins" : "2.332.0"
]

println(versions["jdk"])
versions["jdk"] = "1.11.0"
println(versions)

versions.jenkins = "2.333.0"
println(versions)

println(versions.get("jdk"))
println(versions.keySet())

versions.remove("maven")
println(versions)