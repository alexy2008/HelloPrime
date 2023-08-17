function prime_by_euler(page)
    prime_array = []
    num = falses(page-1)
    for i in 2:page-1
        if !num[i]
            max_prime = i
            push!(prime_array, max_prime)
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
    println("==>",page, " ",length(prime_array)," ", prime_array[end])
    return prime_array
end

function prime_by_eratosthenes_interval(pos, page, prime_array)
    max_ind = 0
    max_prime = 0
    num = falses(page)
    for i in 1:length(prime_array)
        p = prime_array[i]
        if p * p >= pos + page
            break
        end
        for j in ceil(Int64, pos / p):(pos + page - 1) ÷ p
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
    println("==>", pos + page, " ", max_ind," ",  max_prime)

    return max_ind, max_prime
end

function sieve(limit, page)
    # prime_array = []


    pra = prime_by_euler(page)
    max_ind = length(pra)
    max_prime = pra[end]

    for i in 1:div(limit, page) - 1
        ind,p = prime_by_eratosthenes_interval(page * i, page, pra)
        max_ind += ind
        max_prime = p
    end

    return (max_ind,max_prime)
end

println("Hi Prime! I'm Julia :-)")
limit = parse(Int64, ARGS[1])
page = parse(Int64, ARGS[2])
println("Calculate prime numbers up to ", limit," using partitioned Eratosthenes sieve")

@time max_ind,max_prime = sieve(limit, page)

println("Julia finished within ", limit," the ", max_ind,"th prime is ", max_prime)
