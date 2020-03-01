class Prime

  def initialize (limit)
    @is_debug = false
    @max_keep = (Math.sqrt(limit) / Math.log(Math.sqrt(limit)) * 2.5).to_i
    @prime_list = []
    @max_ind = 0
    @prev_no = 0
    @seq_list = []
    @inter_list = []
  end

  def get(index)
    @prime_list[index]
  end

  def size
    @max_ind
  end

  def add(p)
    @prime_list.push(p)
    @max_ind = @max_ind + 1
  end

  def output_interval(inter)
    if inter % 10 ** (inter.to_s.length - 1) == 0
      s = df_string(inter) + "|" + @max_ind.to_s + "|" + @prime_list[@prime_list.length - 1].to_s
      @inter_list.push(s)
      puts "[In:]" + s if @is_debug
    end
  end

  def output_sequence (beginNo, endNo)
    for i in beginNo.to_s.length - 1..endNo.to_s.length - 1
      for j in 1..9
        seq = j * (10 ** i)
        next if seq < beginNo
        return if seq >= endNo
        l = @prime_list[@prime_list.length - 1 - (endNo - seq)]
        s = df_string(seq) + "|" + l.to_s
        @seq_list.push(s)
        puts "==>[No:]" + s if @is_debug
      end
    end
  end

  def free_up
    if @max_ind > @max_keep
      @prime_list = @prime_list[0, @max_keep - 1]
    end
  end

  def generate_results(inter, endNo)
    output_sequence(@prev_no, endNo)
    output_interval(inter)
    @prev_no = endNo
    free_up
  end

  def df_string(l)
    s = l.to_s
    if l % 1_0000_0000_0000 == 0
      s = s[0, s.length - 12] + "万亿"
    elsif l % 1_0000_0000 == 0
      s = s[0, s.length - 8] + "亿"
    elsif l % 1_0000 == 0
      s = s[0, s.length - 4] + "万"
    end
    s
  end

  def print_table
    puts "## 素数区间表"
    puts "区间|个数|最大值"
    puts "---|---|---"
    @inter_list.each { |s| puts s }
    puts "## 素数序列表"
    puts "序号|数值"
    puts "---|---"
    @seq_list.each { |s| puts s }
  end

end

