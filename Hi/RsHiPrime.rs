use std::env;
use std::time::{Duration, SystemTime};

const MAX_KEEP: usize = 80000;
static mut prime_array: [u128; MAX_KEEP + 70000] = [2; MAX_KEEP + 70000];
static mut offset: u128 = 0;
static mut max_ind: u128 = 0;
static mut max_prime: u128 = 0;


unsafe fn prime_by_euler(page: usize) {
    let mut num: Vec<bool> = vec![false; page];
    for i in 2..page - 1 {
        if !num[i] {
            max_prime = i as u128;
            prime_array[(max_ind - offset) as usize] = max_prime;
            max_ind += 1;
        }
        for j in 0..max_ind as usize {
            if i * prime_array[j] as usize >= page { break; }
            num[i * prime_array[j] as usize] = true;
            if i % prime_array[j] as usize == 0 { break; }
        }
    }
}

unsafe fn prime_by_eratosthenes(pos :u128, page :usize){
    let mut num: Vec<bool> = vec![false; page];
    let mut i = 0;

    while prime_array[i] < ((pos + page as u128) as f64).sqrt().ceil() as u128 {
        let p = prime_array[i];
        let mut j = (pos as f64 /p as f64).ceil() as u128 * p;
        while j < pos + page as u128 {
            num[j as usize - pos as usize] = true;
            j += p;
        }
        i+=1;
    }
    for i in 0..num.len() {
        if !num[i] {
            max_prime = pos + i as u128;
            prime_array[(max_ind - offset) as usize] = max_prime;
            max_ind += 1;
        }
    }
}

unsafe fn sieve(limit :u128, page :usize){
    prime_by_euler(page);
    for i in 1..limit/page as u128 {
        prime_by_eratosthenes(page as u128 * i,page);
        if max_ind > MAX_KEEP as u128 {
            offset = max_ind - MAX_KEEP as u128;
        }
    }
}

fn main() {
    println!("Hi Prime! I'm Rust :-)");
    let args: Vec<String> = env::args().collect();
    let limit:u128= args[1].parse().unwrap();
    let page:usize= args[2].parse().unwrap();
    println!("Calculate prime numbers up to {} using partitioned Eratosthenic sieve",limit);
    let start_time = SystemTime::now();
    unsafe { sieve(limit, page); }
    let total_time = SystemTime::now().duration_since(start_time).unwrap().as_millis();
    unsafe { println!("Java finished within {:e} the {}th prime is {}, time cost: {} ms \n",
                      limit as f64, max_ind, max_prime, total_time)};
}

