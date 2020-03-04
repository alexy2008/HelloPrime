Imports System

Module VbHelloPrime
    Function PrimeByEuler(ByVal limit As Long, ByRef prime As Prime) As Long
        Dim top As Long = 0
        Dim num = New Boolean(limit + 1 - 1) {}

        For i = 2 To limit

            If Not num(i) Then
                prime.Add(i)
                top += 1
            End If

            Dim j = 0

            While j < prime.Size() AndAlso CLng(i)*prime.GetItem(j) <= limit
                num(i*prime.GetItem(j)) = True
                If i Mod prime.GetItem(j) = 0 Then Exit While
                j += 1
            End While
        Next

        Return top
    End Function

    Function PrimeByEratosthenesInterval(pos As Long, limit As Long, ByRef prime As Prime) As Long
        Dim top As Long = 0
        Dim num = New Boolean(limit - 1) {}
        Dim i = 0

        While prime.GetItem(i) < Math.Sqrt(pos + limit)
            Dim p As Long = prime.GetItem(i)
            Dim j As Long = CLng((Math.Ceiling(CDbl(pos)/p)*p))

            While j < pos + CLng(limit)
                num(CInt((j - pos))) = True
                j += p
            End While

            i += 1
        End While

        For i = 0 To num.Length - 1

            If Not num(i) Then
                prime.Add(pos + i)
                top += 1
            End If
        Next

        Return top
    End Function


    Sub Main(args As String())
        Console.WriteLine("Hello Prime! I'm VisualBasic.Net :-)")
        Dim page As Long = Int32.Parse(args(0))
        Dim repeat As Long = Int32.Parse(args(1))
        Dim isDebug As Boolean = Boolean.Parse(args(2))
        Dim prime = New Prime(page, repeat, isDebug)
        Dim top As Long = 0
        Console.WriteLine("使用分区埃拉托色尼筛选法计算{0}以内素数", Prime.DfString(page*repeat))
        Dim startTime = DateTime.Now
        top += PrimeByEuler(page, prime)
        prime.GenerateResults(page, top)

        For i = 1 To repeat - 1
            Dim pos As Long = page*CLng(i)
            top += PrimeByEratosthenesInterval(pos, page, prime)
            prime.GenerateResults(pos + page, top)
        Next

        Dim totalTime = CLng((DateTime.Now.Subtract(startTime).TotalMilliseconds))
        prime.PrintTable()
        Console.WriteLine("{0}以内计算完毕。累计耗时 :{1}毫秒", Prime.DfString(page*repeat), totalTime)
    End Sub

    Class Prime
        Private Shared _prime As Long()
        Private Shared _maxInd As Long
        Private _maxKeep As Integer
        Private Shared _offSet As Long
        Private _prevNo As Long
        Private seqList As List(Of String) = New List(Of String)()
        Private interList As List(Of String) = New List(Of String)()
        Private isDebug As Boolean

        Public Sub New(page As Long, repeat As Long, isDbg As Boolean)
            isDebug = isDbg
            _maxKeep = CInt((Math.Sqrt(page*repeat)/Math.Log(Math.Sqrt(page*repeat))*1.3))
            Dim reserve = CInt(((Math.Sqrt(page*repeat) + page)/Math.Log(Math.Sqrt(page*repeat) + page)*1.3))
            _prime = New Long(reserve - 1) {}
            Console.WriteLine("内存分配：" & _maxKeep & " - " & reserve)
        End Sub

        Public Function GetItem (index As Integer) As Long
            Return _prime(index)
        End Function

        Public  Function Size() As Long
            Return _maxInd
        End Function

        Public  Sub Add(p As Long)
            _prime(_maxInd - _offSet) = p
            _maxInd += 1
        End Sub

        Public Sub GenerateResults(inter As Long, endNo As Long)
            PutSequence(_prevNo, endNo)
            PutInterval(inter)
            _prevNo = endNo
            FreeUp()
        End Sub

        Private Sub PutSequence(beginNo As Long, endNo As Long)
            For i As Integer = beginNo.ToString().Length - 1 To endNo.ToString().Length - 1

                For j As Integer = 1 To 10 - 1
                    Dim seq As Long = CLng((j*Math.Pow(10, i) + 0.5))
                    If seq < beginNo Then Continue For
                    If seq >= endNo Then Return
                    Dim l As Long = GetItem(CInt((Size() - _offSet - 1 - (endNo - seq))))
                    Dim s = DfString(seq) & "|" & l
                    seqList.Add(s)
                    If isDebug Then Console.WriteLine("==>[No:] " & s)
                Next
            Next
        End Sub

        Public Sub PutInterval(inter As Long)
            If inter Mod CLng((Math.Pow(10, inter.ToString().Length - 1) + 0.5)) = 0 Then
                Dim ss = DfString(inter) & "|" & _maxInd & "|" & _prime(_maxInd - _offSet - 1)
                interList.Add(ss)
                If isDebug Then Console.WriteLine("[In:]" & ss)
            End If
        End Sub

        Private Sub FreeUp()
            If _maxInd > _maxKeep Then _offSet = _maxInd - _maxKeep
        End Sub

        Public Sub PrintTable()
            Console.WriteLine("## 素数区间表")
            Console.WriteLine("区间|个数|最大值")
            Console.WriteLine("---|---|---")
            interList.ForEach(Sub(s as String) Console.WriteLine(s))
            Console.WriteLine("## 素数序列表")
            Console.WriteLine("序号|数值")
            Console.WriteLine("---|---")
            seqList.ForEach(Sub(s as String) Console.WriteLine(s))
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
