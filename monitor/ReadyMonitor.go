package main

import (
	"net/http"
//	"time"
	"strconv"
  "os/exec"
	"sync"
//	"log"
	"fmt"
	"time"
	"bufio"
	"os"
	"bytes"
)

func main() {
	responsedCh := make(chan string)
	stopCh := make(chan struct{})
	go monitoring(responsedCh, stopCh)
	var isStopClosed = false
	for !isStopClosed {
		select {
		case response := <-responsedCh:
			if(response == "OK"){
				close(stopCh)
				//manualShutDown()
				//shutdownLocalJarKill()
				shutdownEndpoint()
			}
			isStopClosed = true
		default:
		}
	}
	for  {
		if("DONE" == <-responsedCh){
			println("main end")
			return
		}
	}
}

func monitoring(result chan string, stopCh chan struct{}) {
	defer func() { result <- "DONE" }()

	var id = 0
	var monitorStop = false
	wg := sync.WaitGroup{}
	for !monitorStop {
		time.Sleep(50 * time.Millisecond)
		go check(id,result,&wg)
		id ++
		select {
			case <-stopCh:
				println("monitor stop")
			  monitorStop = true
			default:
		}
	}
	wg.Wait()
	println("monitor end")
}

func check(thisId int,result chan string, wg *sync.WaitGroup) {
	wg.Add(1)
	defer wg.Done()
	println("[" + strconv.Itoa(thisId) + "]START")
	resp, err := http.Get("http://localhost:8080/test/?id=" + strconv.Itoa(thisId))
	if (err != nil || resp.StatusCode != 200) {
		println("[" + strconv.Itoa(thisId) + "]アカーン")
		return
	}
	println("[" + strconv.Itoa(thisId) + "]OK")
	result <- "OK"
	defer resp.Body.Close()
}

func manualShutDown() {
	reader := bufio.NewReader(os.Stdin)
	fmt.Print("Enter text: ")
	text, _ := reader.ReadString('\n')
	fmt.Println(text)
}

func shutdownLocalJarKill() {
	println("shutdown")
	cmd := exec.Command("sh", "-c", "kill $(pgrep -f springboot-graceful-shutdown.jar)")
	stdoutStderr,_ := cmd.CombinedOutput()
	fmt.Printf("%s\n", stdoutStderr)
}

func shutdownLocalJarForceKill() {
	println("shutdown")
	cmd := exec.Command("sh", "-c", "kill -9 $(pgrep -f springboot-graceful-shutdown.jar)")
	stdoutStderr,_ := cmd.CombinedOutput()
	fmt.Printf("%s\n", stdoutStderr)
}

func shutdownEndpoint() {
	println("shutdown by endpoint")
	resp, err := http.Post("http://localhost:8080/actuator/shutdown", "", bytes.NewBuffer([]byte("")))
	if (err != nil || resp.StatusCode != 200) {
		panic("shutdonw失敗")
	}
}


type LocalJarApi struct {}
func (self *LocalJarApi) Shutdown(){

}



