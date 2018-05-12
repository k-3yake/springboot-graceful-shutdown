package main


import (
	"net/http"
	"time"
)

func monitoring() {
	resp, err := http.Get("http://192.168.42.110:32698/ready")
	if (err != nil || resp.StatusCode != 200){
		println("[" + time.Now().Format(time.StampMilli) + "]アカーン！!")
		return
	}
	defer resp.Body.Close()
}

func main() {
	for true {
		time.Sleep(50 * time.Millisecond)
		go func() {
		  monitoring()
		}()
	}
}

