use std::env;
use std::time::SystemTime;

const MAX_KEEP: usize = 80000;
static mut PRIME_ARRAY: [usize; MAX_KEEP + 70000] = [2; MAX_KEEP + 70000];
static mut OFFSET: usize = 0;
static mut MAX_IND: usize = 0;
static mut MAX_PRIME: usize = 0;


unsafe fn prime_by_euler(page: usize) {
    let mut num: Vec<bool> = vec![false; page];
    for i in 2..page - 1 {
        if !num[i] {
            MAX_PRIME = i as usize;
            PRIME_ARRAY[(MAX_IND - OFFSET) as usize] = MAX_PRIME;
            MAX_IND += 1;
        }
        for j in 0..MAX_IND as usize {
            if i * PRIME_ARRAY[j] as usize >= page { break; }
            num[i * PRIME_ARRAY[j] as usize] = true;
            if i % PRIME_ARRAY[j] as usize == 0 { break; }
        }
    }
}

unsafe fn prime_by_eratosthenes(pos :usize, page :usize){
    let mut num: Vec<bool> = vec![false; page];
    let mut i = 0;

    while PRIME_ARRAY[i] < ((pos + page as usize) as f64).sqrt().ceil() as usize {
        let p = PRIME_ARRAY[i];
        let mut j = (pos as f64 /p as f64).ceil() as usize * p;
        while j < pos + page as usize {
            num[j as usize - pos as usize] = true;
            j += p;
        }
        i+=1;
    }
    for i in 0..num.len() {
        if !num[i] {
            MAX_PRIME = pos + i as usize;
            PRIME_ARRAY[(MAX_IND - OFFSET) as usize] = MAX_PRIME;
            MAX_IND += 1;
        }
    }
}

unsafe fn sieve(limit :usize, page :usize){
    prime_by_euler(page);
    for i in 1..limit/page as usize {
        prime_by_eratosthenes(page as usize * i,page);
        if MAX_IND > MAX_KEEP as usize {
            OFFSET = MAX_IND - MAX_KEEP as usize;
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
                      limit as f64, MAX_IND, MAX_PRIME, total_time)};
}

