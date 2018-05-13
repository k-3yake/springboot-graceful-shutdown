#目的
以下のものを得たい
・SpringBootでgraceful shutdown	する方法と仕組みの理解

#手段
・簡単なAPIを作成し検証する

#動作確認
アプリケーションの起動
monitorの起動
shutdownを行う
monitorの送信の停止

#DOING
モニターの作りこみ
　モニター開始
　１０個成功したらモニタースレッドにshutdown開始を送りモニター送信を停止
　shutdown
  
  
  
#TODO
shutdownの検証
  kill
  kill -9
  docker stop
  kubernetes