use std::env;
use std::time::SystemTime;

fn prime_by_euler(page: usize) -> (usize, usize, Vec<usize>) {
    let mut num: Vec<bool> = vec![false; page];
    let mut prime_array: Vec<usize> = Vec::new();
    let mut max_ind = 0;
    let mut max_prime: usize = 0;
    for i in 2..page - 1 {
        if !num[i] {
            max_prime = i;
            prime_array.push(max_prime);
            max_ind += 1;
        }
        for j in 0..max_ind {
            if i * prime_array[j] >= page { break; }
            num[i * prime_array[j]] = true;
            if i % prime_array[j] == 0 { break; }
        }
    }
    return (max_ind, max_prime, prime_array)
}

fn prime_by_eratosthenes(pos: usize, page: usize, prime_array: &Vec<usize>) -> (usize, usize) {
    let mut num: Vec<bool> = vec![false; page];
    let mut max_ind = 0;
    let mut max_prime: usize = 0;
    let mut i = 0;
    while *prime_array.get(i).unwrap_or(&(80000usize)) < ((pos + page) as f64).sqrt().ceil() as usize {
        let p = prime_array[i];
        let mut j = (pos as f64 / p as f64).ceil() as usize * p;
        while j < pos + page {
            num[j - pos] = true;
            j += p;
        }
        i += 1;
    }
    for i in 0..num.len() {
        if !num[i] {
            max_prime = pos + i;
            max_ind += 1;
        }
    }
    return (max_ind, max_prime)
}

fn sieve(limit: usize, page: usize) -> (usize, usize) {
    let rp = prime_by_euler(page);
    let mut max_ind = rp.0;
    let mut max_prime = rp.1;
    for i in 1..limit / page {
        let r = prime_by_eratosthenes(page * i, page, &rp.2);
        max_ind += r.0;
        max_prime = r.1;
    }
    return (max_ind, max_prime)
}

fn main() {
    println!("Hi Prime! I'm Rust :-)");
    let args: Vec<String> = env::args().collect();
    let limit: usize = args[1].parse().unwrap();
    let page: usize = args[2].parse().unwrap();
    println!("Calculate prime numbers up to {} using partitioned Eratosthenes sieve", limit);
    let start_time = SystemTime::now();
    let r = sieve(limit, page);
    let total_time = SystemTime::now().duration_since(start_time).unwrap().as_millis();
    println!("Rust finished within {:e} the {}th prime is {}, time cost: {} ms \n",
             limit as f64, r.0, r.1, total_time);
}