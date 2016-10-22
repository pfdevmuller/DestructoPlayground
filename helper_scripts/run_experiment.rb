require 'json'
require_relative 'scrape_for_result.rb'

puts "Deleting old log"
File.delete("destructo_run_log.log")

# Launch:
puts "Launching application"
controller_app = fork do
    exec "java -cp '/Users/mullerp/dev/Mindstorms/DestructoPlayground/DestructoPlayground/target/destructo-playground-1.0-SNAPSHOT-jar-with-dependencies.jar:/Users/mullerp/dev/Mindstorms/lejos/lejos_repo/lejos-ev3/ev3classes/lib/jna-3.2.7.jar' za.co.pietermuller.playground.destructo.Main"
end

Process.detach(controller_app)

# wait for ready:

while not (/yes/ =~ `curl -XPOST 'localhost:8080/testready'`)
    puts "Server not ready yet..."
    sleep 1
end

puts "Starting orders"
`./orders.sh`

puts "All done?"
temp = gets

puts "Quiting"
`curl -XPOST 'localhost:8080/quit'`

scrape_result()
