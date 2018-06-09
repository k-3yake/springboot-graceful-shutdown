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

#DONE
shutdownの検証
  kill
  kill -9
  shutdonw endpoint

#DOING
 gracefulの実装
   https://github.com/gesellix/graceful-shutdown-spring-boot
  
#TODO
shutdownの検証
  kill
  kill -9
  docker stop
  kubernetes