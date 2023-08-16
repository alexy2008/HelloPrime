function prime_by_euler(page, max_ind, max_prime, prime_array)
    num = falses(page)
    for i in 2:page
        if !num[i]
            max_prime = i
            push!(prime_array, max_prime)
            max_ind += 1
        end
        for j in 1:length(prime_array)
            if i * prime_array[j] >= page
                break
            end
            num[i * prime_array[j]] = true
            if i % prime_array[j] == 0
                break
            end
        end
    end
    # println("Calculate prime numbers up to ", limit," using partitioned Eratosthenes sieve")
    println("==>",page, max_ind, max_prime)
    return max_ind, max_prime, prime_array
end

function prime_by_eratosthenes_interval(pos, page, max_ind, max_prime, prime_array)
    num = falses(page)
    for i in 1:length(prime_array)
        p = prime_array[i]
        if p * p >= pos + page
            break
        end
        for j in ceil(Int64, pos / p):(pos + page - 1) ÷ p + 1
            num[j * p - pos + 1] = true
        end
    end

    for i in 1:page
        if !num[i]
            max_prime = pos + i - 1
            # push!(prime_array, max_prime)
            max_ind += 1
        end
    end
    println("", pos + page, max_ind, max_prime)

    return max_ind, max_prime, prime_array
end

function sieve(limit, page)
    prime_array = []
    max_ind = 0
    max_prime = 0

    prime_by_euler(page, max_ind, max_prime, prime_array)

    for i in 1:div(limit, page)
        prime_by_eratosthenes_interval(page * i, page, max_ind, max_prime, prime_array)
        # if max_ind > max_keep:
        #     deleteat!(prime_array,max_keep:length(prime_array))
    end

    return (max_ind=max_ind,max_prime=max_prime)
end

println("Hi Prime! I'm Julia :-)")
limit = parse(Int64, readline())
page = parse(Int64, readline())
println("Calculate prime numbers up to ", limit," using partitioned Eratosthenes sieve")

@time result = sieve(limit, page)

println("Julia finished within ", limit," the ", result.max_ind,"th prime is ", result.max_prime)
