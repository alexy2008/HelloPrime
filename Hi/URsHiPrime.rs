use std::env;
use std::time::{Duration, SystemTime};

const MAX_KEEP: usize = 80000;
static mut prime_array: [usize; MAX_KEEP + 70000] = [2; MAX_KEEP + 70000];
static mut offset: usize = 0;
static mut max_ind: usize = 0;
static mut max_prime: usize = 0;


unsafe fn prime_by_euler(page: usize) {
    let mut num: Vec<bool> = vec![false; page];
    for i in 2..page - 1 {
        if !num[i] {
            max_prime = i as usize;
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

unsafe fn prime_by_eratosthenes(pos :usize, page :usize){
    let mut num: Vec<bool> = vec![false; page];
    let mut i = 0;

    while prime_array[i] < ((pos + page as usize) as f64).sqrt().ceil() as usize {
        let p = prime_array[i];
        let mut j = (pos as f64 /p as f64).ceil() as usize * p;
        while j < pos + page as usize {
            num[j as usize - pos as usize] = true;
            j += p;
        }
        i+=1;
    }
    for i in 0..num.len() {
        if !num[i] {
            max_prime = pos + i as usize;
            prime_array[(max_ind - offset) as usize] = max_prime;
            max_ind += 1;
        }
    }
}

unsafe fn sieve(limit :usize, page :usize){
    prime_by_euler(page);
    for i in 1..limit/page as usize {
        prime_by_eratosthenes(page as usize * i,page);
        if max_ind > MAX_KEEP as usize {
            offset = max_ind - MAX_KEEP as usize;
        }
    }
}

fn main() {
    println!("Hi Prime! I'm Rust :-)");
    let args: Vec<String> = env::args().collect();
    let limit:usize= args[1].parse().unwrap();
    let page:usize= args[2].parse().unwrap();
    println!("Calculate prime numbers up to {} using partitioned Eratosthenic sieve",limit);
    let start_time = SystemTime::now();
    unsafe { sieve(limit, page); }
    let total_time = SystemTime::now().duration_since(start_time).unwrap().as_millis();
    unsafe { println!("Rust finished within {:e} the {}th prime is {}, time cost: {} ms \n",
                      limit as f64, max_ind, max_prime, total_time)};
}

