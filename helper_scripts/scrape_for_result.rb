require 'json'

def scrape_result()
    puts "What is the final x position?"
    x = gets
    puts "What is the final y position?"
    y = gets
    puts "What is the orientation in degrees?"
    d = gets

    result = {}

    result[:x_final] = x.to_f
    result[:y_final] = y.to_f
    result[:d_final] = d.to_f

    result[:x_means] = []
    result[:x_devs] = []
    result[:y_means] = []
    result[:y_devs] = []
    result[:d_means] = []
    result[:d_confs] = []
    File.open("./destructo_run_log.log").each do |line|
        if line =~ /Particle Filter Result/
            comps = line.scan(/X: (Gaussian.*), Y: (Gaussian.*), Orientation: (Angle.*) - za.*/)[0]
            result[:x_means] << comps[0].split(/[,=]/)[1].to_f
            result[:x_devs] << comps[0].split(/[,=]/)[3].to_f
            result[:y_means] << comps[1].split(/[,=]/)[1].to_f
            result[:y_devs] << comps[1].split(/[,=]/)[3].to_f
            result[:d_means] << comps[2].split(/[,=}]/)[2].to_f
            result[:d_confs] << comps[2].split(/[,=}]/)[5].to_f
        end
    end
    File.open("experiment" + Time.now.to_i.to_s + ".json", 'w') { |file| file.write(result.to_json()) }
end
