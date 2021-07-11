$max_keep = 80000
$prime_array = []
$max_ind = 0
$max_prime = 0

def prime_by_euler(page)
  num = Array.new(page, false)
  (2..page - 1).each { |i|
    unless num[i]
      $max_prime = i
      $prime_array.push($max_prime)
      $max_ind += 1
    end
    (0..$prime_array.size).each { |j|
      break if i * $prime_array[j] >= page
      num[i * $prime_array[j]] = true
      break if i % $prime_array[j] == 0
    }
  }
end

def prime_by_eratosthenes_interval(pos, page)
  num = Array.new(page, false )
  (0..(page + pos) ** (1.0 / 2)).each { |i|
    p = $prime_array[i]
    break if p * p >= pos + page
    ((pos * 1.0 / p).ceil * p...pos + page).step(p).each { |j|
      num[j - pos] = true
    }
  }
  (0...page).each { |i|
    unless num[i]
      $max_prime = i + pos
      # $prime_array.push($max_prime)
      $max_ind += 1
    end
  }
end

def sieve(limit, page)
  prime_by_euler(page)
  (1..limit / page - 1).each { |i|
    prime_by_eratosthenes_interval(page * i, page)
    # if $max_ind > $max_keep then $prime_array = $prime_array[0, $max_keep - 1] end
  }
end

puts "Hi Prime! I'm Ruby :-)"
STDOUT.flush
limit = ARGV[0].to_i
page = ARGV[1].to_i

puts "Calculate prime numbers up to #{limit} using partitioned Eratosthenes sieve"
STDOUT.flush

start_time = Time.now
sieve(limit, page)
total_time = ((Time.now - start_time) * 1000).round(0)
puts "Ruby finished within #{"%.0E" % limit} the #{$max_ind}th prime is #{$max_prime}, time cost: #{total_time} ms"