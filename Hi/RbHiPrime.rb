$max_keep = 80000
$prime_array = []
$max_ind = 0
$max_prime = 0

def prime_by_euler(page)
  num = Array.new(page, false)
  for i in 2..page - 1
    if not num[i] then
      $max_prime = i
      $prime_array.push($max_prime)
      $max_ind += 1
    end
    for j in 0..$prime_array.size
      # puts $prime_array[0]
      break if i * $prime_array[j] >= page
      num[i * $prime_array[j]] = true
      break if i % $prime_array[j] == 0
    end
  end
end

def prime_by_eratosthenes_interval(pos, page)
  num = Array.new(page, false )
  for i in 0..(page + pos) ** (1.0 / 2)
    p = $prime_array[i]
    break if p * p >= pos + page
    for j in ((pos * 1.0 / p).ceil * p...pos + page).step(p)
      num[j-pos] = true
    end
  end
  for i in 0...page
    if not num[i] then
      $max_prime = i + pos
      # puts $max_ind+1 ,'--', $max_prime
      $prime_array.push($max_prime)
      $max_ind += 1
    end
  end
end

def sieve(limit, page)
  prime_by_euler(page)
  for i in 1..limit / page - 1
    prime_by_eratosthenes_interval(page * i, page)
    if $max_ind > $max_keep
      # $prime_array = $prime_array[0, $max_keep - 1]
    end
  end
end

puts "Hi Prime! I'm Ruby :-)"
limit = ARGV[0].to_i
page = ARGV[1].to_i

puts "Calculate prime numbers up to #{limit} using partitioned Eratosthenic sieve"

start_time = Time.now
sieve(limit, page)
total_time = ((Time.now - start_time) * 1000).round(0)
puts "Ruby finished within #{"%.0E" % limit} the #{$max_ind}th prime is #{$max_prime}, time cost: #{total_time} ms"

