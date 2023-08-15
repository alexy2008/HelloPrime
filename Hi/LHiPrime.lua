prime_array = {}
max_ind = 0
max_prime = 0

function prime_by_euler(page)
    local num = {}
    for i = 0, page - 1 do num[i] = false end
    for i = 2, page - 1 do
        if not num[i] then
            max_prime = i
            prime_array[max_ind] = max_prime
            max_ind = max_ind + 1
        end
        for j = 0, max_ind do
            if i * prime_array[j] >= page then break end
            num[i * prime_array[j]] = true
            if i % prime_array[j] == 0 then break end
        end
    end
    print("in:",page, max_ind,max_prime)
end

function prime_by_eratosthenes(pos, page)
    local num = {}
    for i = 0, page - 1 do num[i] = false end
    for i = 0, #prime_array - 1 do
        local p = prime_array[i]
        if p == 0 or p * p >= pos + page then break end
        for j = math.ceil(pos / p), pos + page - 1 do
            num[j*p - pos] = true
        end
    end
    for i = 0, page - 1 do
        if not num[i] then
            max_prime = pos + i
            max_ind = max_ind + 1
        end
    end
    print("in:",(pos+page),max_ind,max_prime)
end

function sieve(limit, page)
    prime_by_euler(page)
    for i = 1, (limit / page) - 1 do
        prime_by_eratosthenes(page * i, page)
    end
end


print("Hi Prime! I'm Lua :-)")
local limit = tonumber(arg[1])
local page = tonumber(arg[2])
print("Calculate prime numbers up to ", limit, " using partitioned Eratosthenes sieve")

start_time = os.time()
print(start_time)
sieve(limit,page)
total_time = math.ceil((os.time() - start_time) * 1000)
-- print(os.clock())
print(total_time)
print(string.format("Lua finished within %.0e the %dth prime is %d, time cost: %d ms \n",
limit, max_ind, max_prime, total_time))