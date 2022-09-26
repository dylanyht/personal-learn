//遍历0-9，打印
// for(i=1; i<10; i++){
//     println(i)
// }

// //循环5次
// 5.times{
//     println("hello")
// }

// //循环0-4
// 5.times{i ->
//      println(i)
// }

// //遍历List
// def serverList = ["server-1","server-2","server-3"]

// for (i in serverList){
//     println(i)
// }

//使用each遍历map
def stus = ["zeyang":"177", "jenkins":"199"]
stus.each { k, v ->
       println(k+"="+v)
}

//for遍历map
for (k  in stus.keySet()) {
    println(stus[k])
    println(k+"="+stus[k])
}