Imports System

Module VbHelloPrime
    Dim prm As Prime

    Sub PrimeByEuler(page As Long)
        Dim num = New Boolean(page + 1 - 1) {}
        For i = 2 To page
            If Not num(i) Then prm.Add(i)
            Dim j = 0
            While j < prm.MaxInd AndAlso CLng(i)*prm.GetItem(j) <= page
                num(i*prm.GetItem(j)) = True
                If i Mod prm.GetItem(j) = 0 Then Exit While
                j += 1
            End While
        Next
    End Sub

    Sub PrimeByEratosthenes(pos As Long, page As Long)
        Dim num = New Boolean(page - 1) {}
        Dim i = 0
        While prm.GetItem(i) < Math.Sqrt(pos + page)
            Dim p As Long = prm.GetItem(i)
            Dim j As Long = CLng((Math.Ceiling(CDbl(pos)/p)*p))

            While j < pos + CLng(page)
                num(CInt((j - pos))) = True
                j += p
            End While
            i += 1
        End While

        For i = 0 To num.Length - 1
            If Not num(i) Then prm.Add(pos + i)
        Next
    End Sub

    Sub Main(args As String())
        Console.WriteLine("Hello Prime! I'm Visual Basic.Net :-)")
        Dim limit As Long = Int64.Parse(args(0))
        Dim page As Long = Int64.Parse(args(1))
        Dim mode As Integer = Integer.Parse(args(2))
        prm = New Prime(limit, page, mode)

        Console.WriteLine("使用分区埃拉托色尼筛选法计算{0}以内素数", Prime.DfString(limit))
        Dim startTime = DateTime.Now
        PrimeByEuler(page)
        prm.GenerateResults(page)

        For i = 1 To limit/page - 1
            Dim pos As Long = page*i
            PrimeByEratosthenes(pos, page)
            prm.GenerateResults(pos + page)
        Next

        Dim totalTime = CLng((DateTime.Now.Subtract(startTime).TotalMilliseconds))
        prm.PrintTable()
        Console.WriteLine("VB.net finished within {0:0.#e+00} the {1}th prime is {2}, cost time:{3}ms",
                          limit, prm.MaxInd, prm.MaxPrime, totalTime)
    End Sub

    Class Prime
        Public MaxInd As Long
        Public MaxPrime As Long

        Private _prime As Long()

        Private _maxKeep As Integer
        Private Shared _offSet As Long
        Private _prevNo As Long
        Private seqList As List(Of String) = New List(Of String)()
        Private interList As List(Of String) = New List(Of String)()
        Private _mode As Integer

        Public Sub New(limit As Long, page As Long, mode As Integer)
            _mode = mode
            _maxKeep = CInt((Math.Sqrt(limit)/Math.Log(Math.Sqrt(limit))*1.3))
            Dim reserve = CInt(((Math.Sqrt(limit) + page)/Math.Log(Math.Sqrt(limit) + page)*1.3))
            _prime = New Long(reserve - 1) {}
            Console.WriteLine("内存分配：" & _maxKeep & " - " & reserve)
        End Sub

        Public Function GetItem(index As Integer) As Long
            Return _prime(index)
        End Function

        Public Sub Add(p As Long)
            _prime(MaxInd - _offSet) = p
            MaxInd += 1
        End Sub

        Public Sub GenerateResults(inter As Long)
            If _mode > 0 Then
                PutSequence(_prevNo)
                PutInterval(inter)
                _prevNo = MaxInd
            End If
            MaxPrime = _prime(MaxInd - _offSet - 1)
            FreeUp()
        End Sub

        Private Sub PutSequence(beginNo As Long)
            For i As Integer = beginNo.ToString().Length - 1 To MaxInd.ToString().Length - 1
                For j As Integer = 1 To 10 - 1
                    Dim seq As Long = CLng((j*Math.Pow(10, i) + 0.5))
                    If seq < beginNo Then Continue For
                    If seq >= MaxInd Then Return
                    Dim l As Long = GetItem(seq - _offSet - 1)
                    Dim s = DfString(seq) & "|" & l
                    seqList.Add(s)
                    If _mode > 1 Then Console.WriteLine("==>[No:] " & s)
                Next
            Next
        End Sub

        Public Sub PutInterval(inter As Long)
            If inter Mod CLng((Math.Pow(10, inter.ToString().Length - 1) + 0.5)) = 0 Then
                Dim ss = DfString(inter) & "|" & MaxInd & "|" & _prime(MaxInd - _offSet - 1)
                interList.Add(ss)
                If _mode > 1 Then Console.WriteLine("[In:]" & ss)
            End If
        End Sub

        Private Sub FreeUp()
            If MaxInd > _maxKeep Then _offSet = MaxInd - _maxKeep
        End Sub

        Public Sub PrintTable()
            If _mode < 1 Then Exit Sub
            Console.WriteLine("## 素数序列表")
            Console.WriteLine("序号|数值")
            Console.WriteLine("---|---")
            seqList.ForEach(Sub(s as String) Console.WriteLine(s))
            Console.WriteLine("## 素数区间表")
            Console.WriteLine("区间|个数|最大值")
            Console.WriteLine("---|---|---")
            interList.ForEach(Sub(s as String) Console.WriteLine(s))
        End Sub

        Public Shared Function DfString(l As Long) As String
            Dim s As String = l.ToString()

            If l Mod 10000_0000_0000L = 0 Then
                s = s.Substring(0, s.Length - 12) & "万亿"
            ElseIf l Mod 10000_0000L = 0 Then
                s = s.Substring(0, s.Length - 8) & "亿"
            ElseIf l Mod 10000 = 0 Then
                s = s.Substring(0, s.Length - 4) & "万"
            End If

            Return s
        End Function
    End Class
End Module
