require_relative 'prime'

def prime_by_euler(limit, prime)
  top = 0
  num = Array.new(limit, true)
  (2..limit - 1).each { |i|
    if num[i] then
      prime.add(i); top = top + 1
    end
    (0..prime.size).each { |j|
      break if i * prime.get(j) >= limit
      num[i * prime.get(j)] = false
      break if i % prime.get(j) == 0
    }
  }
  top
end

def prime_by_eratosthenes_interval(pos, limit, prime)
  top = 0
  num = Array.new(limit, true)
  (0..(limit + pos) ** (1.0 / 2)).each { |i|
    p = prime.get(i)
    break if p * p >= pos + limit
    ((pos * 1.0 / p).ceil * p...pos + limit).step(p).each { |j|
      num[j - pos] = false
    }
  }
  (0...num.length).each { |i|
    if num[i] then
      prime.add(i + pos); top = top + 1
    end
  }
  top
end

puts "Hello Prime! I'm Ruby :-)"
PAGE = 1_0000
repeat = 1_0000
limit = PAGE*repeat
top = 0
prime = Prime.new(PAGE*repeat)

puts "使用分区埃拉托色尼筛选法计算#{prime.df_string(limit)}以内素数："
start_time = Time.now
top += prime_by_euler PAGE, prime
prime.generate_results PAGE, top
(1..repeat).each do |i|
  pos = PAGE * i
  top += prime_by_eratosthenes_interval pos, PAGE, prime
  prime.generate_results pos + PAGE, top
end

total_time = ((Time.now-start_time)*1000).round(0)
prime.print_table
puts "#{prime.df_string limit}以内计算完毕。累计耗时：#{total_time}豪秒"
