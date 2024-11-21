; ModuleID = 'llvm-link'
source_filename = "llvm-link"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@.str.0 = private unnamed_addr constant [5 x i8] c"j : \00", align 1
@.str.1 = private unnamed_addr constant [7 x i8] c", k : \00", align 1
@.str.2 = private unnamed_addr constant [7 x i8] c", l : \00", align 1
@.str.3 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.4 = private unnamed_addr constant [6 x i8] c"a! = \00", align 1
@.str.5 = private unnamed_addr constant [9 x i8] c", num = \00", align 1
@.str.6 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.7 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.8 = private unnamed_addr constant [15 x i8] c"test---------\0A\00", align 1
@.str.9 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.10 = private unnamed_addr constant [31 x i8] c"scanf a, b to get gcd and lcm\0A\00", align 1
@.str.11 = private unnamed_addr constant [8 x i8] c"gcd is \00", align 1
@.str.12 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.13 = private unnamed_addr constant [8 x i8] c"lcm is \00", align 1
@.str.14 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.15 = private unnamed_addr constant [26 x i8] c"scanf a to get Fibonacci\0A\00", align 1
@.str.16 = private unnamed_addr constant [8 x i8] c"fib is \00", align 1
@.str.17 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str = private unnamed_addr constant [3 x i8] c"%c\00", align 1
@.str.1.3 = private unnamed_addr constant [3 x i8] c"%d\00", align 1
@.str.2.6 = private unnamed_addr constant [4 x i8] c"%d:\00", align 1
@.str.3.7 = private unnamed_addr constant [4 x i8] c" %d\00", align 1
@.str.4.8 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.5.11 = private unnamed_addr constant [3 x i8] c"%s\00", align 1

define dso_local void @fun1() {
  %1 = alloca i32, align 4
  %2 = alloca i32, align 4
  store i32 4, i32* %2, align 4
  %3 = alloca i32, align 4
  store i32 6, i32* %3, align 4
  %4 = load i32, i32* %2, align 4
  store i32 %4, i32* %3, align 4
  store i32 3, i32* %1, align 4
  %5 = load i32, i32* %1, align 4
  %6 = srem i32 %5, 20
  %7 = sub i32 %6, 1
  %8 = add i32 %7, 9
  store i32 %8, i32* %1, align 4
  %9 = alloca i32, align 4
  store i32 0, i32* %9, align 4
  br label %10

10:                                               ; preds = %29, %0
  %11 = load i32, i32* %9, align 4
  %12 = icmp sle i32 %11, 7
  %13 = zext i1 %12 to i32
  %14 = icmp ne i32 %13, 0
  br i1 %14, label %15, label %30

15:                                               ; preds = %10
  %16 = load i32, i32* %9, align 4
  %17 = add i32 %16, 1
  store i32 %17, i32* %9, align 4
  %18 = load i32, i32* %1, align 4
  %19 = load i32, i32* %9, align 4
  %20 = icmp eq i32 %18, %19
  br i1 %20, label %21, label %26

21:                                               ; preds = %15
  %22 = load i32, i32* %2, align 4
  %23 = load i32, i32* %1, align 4
  %24 = add i32 %22, %23
  store i32 %24, i32* %2, align 4
  br label %29

25:                                               ; No predecessors!
  br label %26

26:                                               ; preds = %25, %15
  %27 = load i32, i32* %1, align 4
  %28 = sub i32 %27, 1
  store i32 %28, i32* %1, align 4
  br label %29

29:                                               ; preds = %26, %21
  br label %10

30:                                               ; preds = %10
  call void @putstr(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @.str.0, i64 0, i64 0))
  %31 = load i32, i32* %1, align 4
  call void @putint(i32 %31)
  call void @putstr(i8* getelementptr inbounds ([7 x i8], [7 x i8]* @.str.1, i64 0, i64 0))
  %32 = load i32, i32* %2, align 4
  call void @putint(i32 %32)
  call void @putstr(i8* getelementptr inbounds ([7 x i8], [7 x i8]* @.str.2, i64 0, i64 0))
  %33 = load i32, i32* %3, align 4
  call void @putint(i32 %33)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.3, i64 0, i64 0))
  ret void
}

define dso_local i32 @fun2(i32 %0) {
  %2 = alloca i32, align 4
  store i32 %0, i32* %2, align 4
  %3 = alloca i32, align 4
  store i32 1, i32* %3, align 4
  %4 = alloca i32, align 4
  store i32 1, i32* %4, align 4
  br label %5

5:                                                ; preds = %28, %1
  %6 = load i32, i32* %2, align 4
  %7 = icmp sge i32 %6, 1
  %8 = zext i1 %7 to i32
  %9 = icmp ne i32 %8, 0
  br i1 %9, label %10, label %29

10:                                               ; preds = %5
  %11 = load i32, i32* %3, align 4
  %12 = load i32, i32* %2, align 4
  %13 = mul i32 %11, %12
  store i32 %13, i32* %3, align 4
  %14 = load i32, i32* %2, align 4
  %15 = sub i32 %14, 1
  store i32 %15, i32* %2, align 4
  %16 = load i32, i32* %2, align 4
  %17 = icmp eq i32 %16, 1
  br i1 %17, label %18, label %20

18:                                               ; preds = %10
  br label %29

19:                                               ; No predecessors!
  br label %27

20:                                               ; preds = %10
  %21 = load i32, i32* %2, align 4
  %22 = icmp ne i32 %21, 1
  br i1 %22, label %23, label %26

23:                                               ; preds = %20
  %24 = load i32, i32* %4, align 4
  %25 = add i32 %24, 1
  store i32 %25, i32* %4, align 4
  br label %26

26:                                               ; preds = %23, %20
  br label %27

27:                                               ; preds = %26, %19
  br label %28

28:                                               ; preds = %27
  br label %5

29:                                               ; preds = %18, %5
  call void @putstr(i8* getelementptr inbounds ([6 x i8], [6 x i8]* @.str.4, i64 0, i64 0))
  %30 = load i32, i32* %3, align 4
  call void @putint(i32 %30)
  call void @putstr(i8* getelementptr inbounds ([9 x i8], [9 x i8]* @.str.5, i64 0, i64 0))
  %31 = load i32, i32* %4, align 4
  call void @putint(i32 %31)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.6, i64 0, i64 0))
  ret i32 1
}

define dso_local i32 @fun3(i32 %0, i32 %1) {
  %3 = alloca i32, align 4
  store i32 %0, i32* %3, align 4
  %4 = alloca i32, align 4
  store i32 %1, i32* %4, align 4
  %5 = load i32, i32* %3, align 4
  %6 = load i32, i32* %4, align 4
  %7 = icmp sgt i32 %5, %6
  %8 = zext i1 %7 to i32
  %9 = icmp ne i32 %8, 0
  br i1 %9, label %10, label %13

10:                                               ; preds = %2
  %11 = load i32, i32* %3, align 4
  ret i32 %11

12:                                               ; No predecessors!
  br label %23

13:                                               ; preds = %2
  %14 = load i32, i32* %3, align 4
  %15 = load i32, i32* %4, align 4
  %16 = icmp slt i32 %14, %15
  %17 = zext i1 %16 to i32
  %18 = icmp ne i32 %17, 0
  br i1 %18, label %19, label %22

19:                                               ; preds = %13
  %20 = load i32, i32* %4, align 4
  ret i32 %20

21:                                               ; No predecessors!
  br label %22

22:                                               ; preds = %21, %13
  br label %23

23:                                               ; preds = %22, %12
  %24 = load i32, i32* %3, align 4
  ret i32 %24
}

define dso_local i32 @gcd(i32 %0, i32 %1) {
  %3 = alloca i32, align 4
  store i32 %0, i32* %3, align 4
  %4 = alloca i32, align 4
  store i32 %1, i32* %4, align 4
  %5 = load i32, i32* %3, align 4
  %6 = load i32, i32* %4, align 4
  %7 = srem i32 %5, %6
  %8 = icmp eq i32 %7, 0
  br i1 %8, label %9, label %12

9:                                                ; preds = %2
  %10 = load i32, i32* %4, align 4
  ret i32 %10

11:                                               ; No predecessors!
  br label %12

12:                                               ; preds = %11, %2
  %13 = load i32, i32* %4, align 4
  %14 = load i32, i32* %3, align 4
  %15 = load i32, i32* %4, align 4
  %16 = srem i32 %14, %15
  %17 = call i32 @gcd(i32 %13, i32 %16)
  ret i32 %17
}

define dso_local i32 @lcm(i32 %0, i32 %1) {
  %3 = alloca i32, align 4
  store i32 %0, i32* %3, align 4
  %4 = alloca i32, align 4
  store i32 %1, i32* %4, align 4
  %5 = alloca i32, align 4
  %6 = load i32, i32* %3, align 4
  %7 = load i32, i32* %4, align 4
  %8 = call i32 @gcd(i32 %6, i32 %7)
  store i32 %8, i32* %5, align 4
  %9 = load i32, i32* %3, align 4
  %10 = load i32, i32* %4, align 4
  %11 = mul i32 %9, %10
  %12 = load i32, i32* %5, align 4
  %13 = sdiv i32 %11, %12
  ret i32 %13
}

define dso_local void @fun4(i32 %0, i32 %1, i32 %2) {
  %4 = alloca i32, align 4
  store i32 %0, i32* %4, align 4
  %5 = alloca i32, align 4
  store i32 %1, i32* %5, align 4
  %6 = alloca i32, align 4
  store i32 %2, i32* %6, align 4
  %7 = alloca i32, align 4
  %8 = load i32, i32* %4, align 4
  %9 = load i32, i32* %5, align 4
  %10 = add i32 %8, %9
  %11 = load i32, i32* %6, align 4
  %12 = sub i32 %10, %11
  %13 = load i32, i32* %4, align 4
  %14 = mul i32 %12, %13
  store i32 %14, i32* %7, align 4
  %15 = load i32, i32* %7, align 4
  call void @putint(i32 %15)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.7, i64 0, i64 0))
  ret void
}

define dso_local i32 @fun5(i32 %0) {
  %2 = alloca i32, align 4
  store i32 %0, i32* %2, align 4
  %3 = load i32, i32* %2, align 4
  %4 = icmp eq i32 %3, 1
  br i1 %4, label %5, label %7

5:                                                ; preds = %1
  ret i32 1

6:                                                ; No predecessors!
  br label %13

7:                                                ; preds = %1
  %8 = load i32, i32* %2, align 4
  %9 = icmp eq i32 %8, 2
  br i1 %9, label %10, label %12

10:                                               ; preds = %7
  ret i32 1

11:                                               ; No predecessors!
  br label %12

12:                                               ; preds = %11, %7
  br label %13

13:                                               ; preds = %12, %6
  %14 = load i32, i32* %2, align 4
  %15 = sub i32 %14, 1
  %16 = call i32 @fun5(i32 %15)
  %17 = load i32, i32* %2, align 4
  %18 = sub i32 %17, 2
  %19 = call i32 @fun5(i32 %18)
  %20 = add i32 %16, %19
  ret i32 %20
}

define dso_local i32 @main() {
  call void @fun1()
  %1 = call i32 @fun2(i32 6)
  call void @putstr(i8* getelementptr inbounds ([15 x i8], [15 x i8]* @.str.8, i64 0, i64 0))
  %2 = call i32 @fun3(i32 3, i32 6)
  %3 = call i32 @fun3(i32 2, i32 %2)
  call void @putint(i32 %3)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.9, i64 0, i64 0))
  %4 = alloca i32, align 4
  %5 = alloca i32, align 4
  call void @putstr(i8* getelementptr inbounds ([31 x i8], [31 x i8]* @.str.10, i64 0, i64 0))
  %6 = call i32 @getint()
  store i32 %6, i32* %4, align 4
  %7 = call i32 @getint()
  store i32 %7, i32* %5, align 4
  call void @putstr(i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.str.11, i64 0, i64 0))
  %8 = load i32, i32* %4, align 4
  %9 = load i32, i32* %5, align 4
  %10 = call i32 @gcd(i32 %8, i32 %9)
  call void @putint(i32 %10)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.12, i64 0, i64 0))
  call void @putstr(i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.str.13, i64 0, i64 0))
  %11 = load i32, i32* %4, align 4
  %12 = load i32, i32* %5, align 4
  %13 = call i32 @lcm(i32 %11, i32 %12)
  call void @putint(i32 %13)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.14, i64 0, i64 0))
  %14 = alloca i32, align 4
  %15 = call i32 @getint()
  store i32 %15, i32* %14, align 4
  %16 = load i32, i32* %14, align 4
  call void @fun4(i32 %16, i32 3, i32 10)
  call void @putstr(i8* getelementptr inbounds ([26 x i8], [26 x i8]* @.str.15, i64 0, i64 0))
  %17 = alloca i32, align 4
  %18 = call i32 @getint()
  store i32 %18, i32* %17, align 4
  call void @putstr(i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.str.16, i64 0, i64 0))
  %19 = load i32, i32* %17, align 4
  %20 = call i32 @fun5(i32 %19)
  call void @putint(i32 %20)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.17, i64 0, i64 0))
  ret i32 0
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @getchar() #0 {
  %1 = alloca i8, align 1
  %2 = call i32 (i8*, ...) @__isoc99_scanf(i8* noundef getelementptr inbounds ([3 x i8], [3 x i8]* @.str, i64 0, i64 0), i8* noundef %1)
  %3 = load i8, i8* %1, align 1
  %4 = sext i8 %3 to i32
  ret i32 %4
}

declare i32 @__isoc99_scanf(i8* noundef, ...) #1

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @getint() #0 {
  %1 = alloca i32, align 4
  %2 = call i32 (i8*, ...) @__isoc99_scanf(i8* noundef getelementptr inbounds ([3 x i8], [3 x i8]* @.str.1.3, i64 0, i64 0), i32* noundef %1)
  br label %3

3:                                                ; preds = %6, %0
  %4 = call i32 @getchar()
  %5 = icmp ne i32 %4, 10
  br i1 %5, label %6, label %7

6:                                                ; preds = %3
  br label %3, !llvm.loop !6

7:                                                ; preds = %3
  %8 = load i32, i32* %1, align 4
  ret i32 %8
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local i32 @getarray(i32* noundef %0) #0 {
  %2 = alloca i32*, align 8
  %3 = alloca i32, align 4
  %4 = alloca i32, align 4
  store i32* %0, i32** %2, align 8
  %5 = call i32 (i8*, ...) @__isoc99_scanf(i8* noundef getelementptr inbounds ([3 x i8], [3 x i8]* @.str.1.3, i64 0, i64 0), i32* noundef %3)
  store i32 0, i32* %4, align 4
  br label %6

6:                                                ; preds = %16, %1
  %7 = load i32, i32* %4, align 4
  %8 = load i32, i32* %3, align 4
  %9 = icmp slt i32 %7, %8
  br i1 %9, label %10, label %19

10:                                               ; preds = %6
  %11 = load i32*, i32** %2, align 8
  %12 = load i32, i32* %4, align 4
  %13 = sext i32 %12 to i64
  %14 = getelementptr inbounds i32, i32* %11, i64 %13
  %15 = call i32 (i8*, ...) @__isoc99_scanf(i8* noundef getelementptr inbounds ([3 x i8], [3 x i8]* @.str.1.3, i64 0, i64 0), i32* noundef %14)
  br label %16

16:                                               ; preds = %10
  %17 = load i32, i32* %4, align 4
  %18 = add nsw i32 %17, 1
  store i32 %18, i32* %4, align 4
  br label %6, !llvm.loop !8

19:                                               ; preds = %6
  %20 = load i32, i32* %3, align 4
  ret i32 %20
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @putint(i32 noundef %0) #0 {
  %2 = alloca i32, align 4
  store i32 %0, i32* %2, align 4
  %3 = load i32, i32* %2, align 4
  %4 = call i32 (i8*, ...) @printf(i8* noundef getelementptr inbounds ([3 x i8], [3 x i8]* @.str.1.3, i64 0, i64 0), i32 noundef %3)
  ret void
}

declare i32 @printf(i8* noundef, ...) #1

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @putch(i32 noundef %0) #0 {
  %2 = alloca i32, align 4
  store i32 %0, i32* %2, align 4
  %3 = load i32, i32* %2, align 4
  %4 = call i32 (i8*, ...) @printf(i8* noundef getelementptr inbounds ([3 x i8], [3 x i8]* @.str, i64 0, i64 0), i32 noundef %3)
  ret void
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @putarray(i32 noundef %0, i32* noundef %1) #0 {
  %3 = alloca i32, align 4
  %4 = alloca i32*, align 8
  %5 = alloca i32, align 4
  store i32 %0, i32* %3, align 4
  store i32* %1, i32** %4, align 8
  %6 = load i32, i32* %3, align 4
  %7 = call i32 (i8*, ...) @printf(i8* noundef getelementptr inbounds ([4 x i8], [4 x i8]* @.str.2.6, i64 0, i64 0), i32 noundef %6)
  store i32 0, i32* %5, align 4
  br label %8

8:                                                ; preds = %19, %2
  %9 = load i32, i32* %5, align 4
  %10 = load i32, i32* %3, align 4
  %11 = icmp slt i32 %9, %10
  br i1 %11, label %12, label %22

12:                                               ; preds = %8
  %13 = load i32*, i32** %4, align 8
  %14 = load i32, i32* %5, align 4
  %15 = sext i32 %14 to i64
  %16 = getelementptr inbounds i32, i32* %13, i64 %15
  %17 = load i32, i32* %16, align 4
  %18 = call i32 (i8*, ...) @printf(i8* noundef getelementptr inbounds ([4 x i8], [4 x i8]* @.str.3.7, i64 0, i64 0), i32 noundef %17)
  br label %19

19:                                               ; preds = %12
  %20 = load i32, i32* %5, align 4
  %21 = add nsw i32 %20, 1
  store i32 %21, i32* %5, align 4
  br label %8, !llvm.loop !9

22:                                               ; preds = %8
  %23 = call i32 (i8*, ...) @printf(i8* noundef getelementptr inbounds ([2 x i8], [2 x i8]* @.str.4.8, i64 0, i64 0))
  ret void
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @putstr(i8* noundef %0) #0 {
  %2 = alloca i8*, align 8
  store i8* %0, i8** %2, align 8
  %3 = load i8*, i8** %2, align 8
  %4 = call i32 (i8*, ...) @printf(i8* noundef getelementptr inbounds ([3 x i8], [3 x i8]* @.str.5.11, i64 0, i64 0), i8* noundef %3)
  ret void
}

attributes #0 = { noinline nounwind optnone uwtable "frame-pointer"="all" "min-legal-vector-width"="0" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }
attributes #1 = { "frame-pointer"="all" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "target-cpu"="x86-64" "target-features"="+cx8,+fxsr,+mmx,+sse,+sse2,+x87" "tune-cpu"="generic" }

!llvm.ident = !{!0}
!llvm.module.flags = !{!1, !2, !3, !4, !5}

!0 = !{!"Ubuntu clang version 14.0.0-1ubuntu1.1"}
!1 = !{i32 1, !"wchar_size", i32 4}
!2 = !{i32 7, !"PIC Level", i32 2}
!3 = !{i32 7, !"PIE Level", i32 2}
!4 = !{i32 7, !"uwtable", i32 1}
!5 = !{i32 7, !"frame-pointer", i32 2}
!6 = distinct !{!6, !7}
!7 = !{!"llvm.loop.mustprogress"}
!8 = distinct !{!8, !7}
!9 = distinct !{!9, !7}
