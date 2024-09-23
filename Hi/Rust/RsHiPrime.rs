use std::env;
use std::time::SystemTime;

fn prime_by_euler(page: usize) -> Vec<usize> {
    let mut sieve: Vec<bool> = vec![false; page];
    let mut prime_array: Vec<usize> = Vec::new();
    for i in 2..page {
        if !sieve[i] { prime_array.push(i) }
        let mut j = 0;
        while i * prime_array[j] < page {
            sieve[i * prime_array[j]] = true;
            if i % prime_array[j] == 0 { break }
            j += 1;
        }
    }
    prime_array
}

fn prime_by_eratosthenes(pos: usize, page: usize, prime_array: &Vec<usize>) -> (usize, usize) {
    let mut sieve: Vec<bool> = vec![false; page];
    let mut max_ind = 0;
    let mut max_prime = 0;
    let sqr_limit = ((pos + page) as f64).sqrt().ceil() as usize;
    let mut i = 0;

    while i < prime_array.len() && prime_array[i] < sqr_limit {
        let p = prime_array[i];
        let mut j = ((pos + p -1) / p ) * p; 
        while j  < pos + page {
            sieve[j  - pos] = true;
            j += p;
        }
        i += 1;
    }
    
    for i in 1..=page / 2 {
        if !sieve[2 * i - 1] {
            max_prime = pos + 2 * i - 1;
            max_ind += 1;
        }
    }
    (max_ind, max_prime)
}

fn calculate(limit: usize, page: usize) -> (usize, usize) {
    let mut n = 1;
    while page * n < (limit as f64).sqrt() as usize {
        n += 1;
    }
    
    let primer_list = prime_by_euler(page * n);
    let mut max_ind = primer_list.len();
    let mut max_prime = primer_list[max_ind - 1];

    for i in n..(limit / page) {
        let (local_max_ind, local_max_prime) = prime_by_eratosthenes(page * i, page, &primer_list);
        max_ind += local_max_ind;
        max_prime = local_max_prime;
    }


    (max_ind, max_prime)
}

fn main() {
    println!("Hi Prime! I'm Rust :-)");
    let args: Vec<String> = env::args().collect();

    let limit: usize = args[1].parse().unwrap();
    let page: usize = args[2].parse().unwrap();

    println!("Calculate prime numbers up to {} using partitioned Eratosthenes sieve", limit);
    let start_time = SystemTime::now();
    let r = calculate(limit, page);
    let total_time = SystemTime::now().duration_since(start_time).unwrap().as_millis();

    println!("Rust finished; the {}th prime is {}, time cost: {} ms \n", r.0, r.1, total_time);
}