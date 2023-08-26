use std::env;
use std::sync::mpsc;
use std::thread;
use std::thread::JoinHandle;
use std::time::SystemTime;

fn prime_by_euler(page: usize) -> Vec<usize> {
    let mut sieve: Vec<bool> = vec![false; page];
    let mut prime_array: Vec<usize> = Vec::new();
    for i in 2..page {
        if !sieve[i] { prime_array.push(i) }
        let mut j = 0;
        while i * prime_array[j] < page {
            sieve[i * prime_array[j]] = true;
            if i % prime_array[j] == 0 { break; }
            j = j + 1;
        }
    }
    return prime_array;
}

fn prime_by_eratosthenes(pos: usize, page: usize, prime_array: &Vec<usize>) -> (usize, usize) {
    let mut sieve: Vec<bool> = vec![false; page];
    let mut max_ind = 0;
    let mut max_prime = 0;
    let sqr_limit = ((pos + page) as f64).sqrt().ceil() as usize;
    let mut i = 0;
    while prime_array[i] < sqr_limit {
        let p = prime_array[i];
        let mut j = ((pos + p -1) / p ) * p; 
        while j  < pos + page {
            sieve[j  - pos] = true;
            j += p;
        }
        i += 1;
        if i >= prime_array.len() {break;}
    }
    for i in 1..=page / 2 {
        if !sieve[2 * i - 1] {
            max_prime = pos + 2 * i - 1;
            max_ind += 1;
        }
    }
    return (max_ind, max_prime);
}

fn calculate(limit: usize, page: usize, thread_number: usize) -> (usize, usize) {
    let primer_list = prime_by_euler(page);
    let mut max_ind = primer_list.len();
    let mut max_prime = primer_list[max_ind - 1];
    let mut task: Vec<JoinHandle<()>> = Vec::new();
    let (tx, rx) = mpsc::channel();
    for i in 0..thread_number {
        let pl = primer_list.clone();
        let ltx = tx.clone();
        task.push(thread::spawn(move || {
            let mut local_max_prime = 0;
            let mut local_max_ind = 0;
            let mut j = i + 1;
            while j < limit / page {
                let r = prime_by_eratosthenes(page * j, page, &pl);
                local_max_ind += r.0;
                local_max_prime = r.1;
                j += thread_number;
            }
            ltx.send((local_max_ind, local_max_prime)).unwrap();
        }));
    }
    for t in task { t.join().unwrap(); }
    for _ in 0..thread_number {
        let r = rx.recv().unwrap();
        max_ind += r.0;
        if max_prime < r.1 { max_prime = r.1 };
    }
    return (max_ind, max_prime);
}

fn main() {
    println!("Hello Prime! I'm Rust :-)");
    let args: Vec<String> = env::args().collect();
    let limit: usize = args[1].parse().unwrap();
    let page: usize = args[2].parse().unwrap();
    let thread_number: usize = args[4].parse().unwrap();
    println!("Calculate prime numbers up to {} using partitioned Eratosthenes sieve", limit);
    let start_time = SystemTime::now();
    let r = calculate(limit, page, thread_number);
    let total_time = SystemTime::now().duration_since(start_time).unwrap().as_millis();
    println!("Rust using {} thread(s) finished within {:e} the {}th prime is {}, time cost: {} ms \n",
             thread_number, limit as f64, r.0, r.1, total_time);
}