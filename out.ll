; ModuleID = 'llvm-link'
source_filename = "llvm-link"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@p = dso_local constant i32 88
@q = dso_local global i32 66
@r = dso_local global i32 155
@s = dso_local global i32 15
@.str.0 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.1 = private unnamed_addr constant [24 x i8] c"Your Num is too Big!!!\0A\00", align 1
@.str.2 = private unnamed_addr constant [5 x i8] c"ret=\00", align 1
@.str.3 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.4 = private unnamed_addr constant [10 x i8] c"19373022\0A\00", align 1
@.str.5 = private unnamed_addr constant [3 x i8] c"b:\00", align 1
@.str.6 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.7 = private unnamed_addr constant [17 x i8] c"Bool1 is false!\0A\00", align 1
@.str.8 = private unnamed_addr constant [14 x i8] c"Good!,Num is \00", align 1
@.str.9 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.10 = private unnamed_addr constant [6 x i8] c"Oh!?\0A\00", align 1
@.str.11 = private unnamed_addr constant [8 x i8] c"mid is \00", align 1
@.str.12 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.13 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str = private unnamed_addr constant [3 x i8] c"%c\00", align 1
@.str.1.3 = private unnamed_addr constant [3 x i8] c"%d\00", align 1
@.str.2.6 = private unnamed_addr constant [4 x i8] c"%d:\00", align 1
@.str.3.7 = private unnamed_addr constant [4 x i8] c" %d\00", align 1
@.str.4.8 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.5.11 = private unnamed_addr constant [3 x i8] c"%s\00", align 1

define dso_local i32 @max(i32 %0, i32 %1) {
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
  br label %16

13:                                               ; preds = %2
  %14 = load i32, i32* %4, align 4
  ret i32 %14

15:                                               ; No predecessors!
  br label %16

16:                                               ; preds = %15, %12
  %17 = load i32, i32* %3, align 4
  ret i32 %17
}

define dso_local i32 @min(i32 %0, i32 %1) {
  %3 = alloca i32, align 4
  store i32 %0, i32* %3, align 4
  %4 = alloca i32, align 4
  store i32 %1, i32* %4, align 4
  %5 = load i32, i32* %3, align 4
  %6 = load i32, i32* %4, align 4
  %7 = icmp slt i32 %5, %6
  %8 = zext i1 %7 to i32
  %9 = icmp ne i32 %8, 0
  br i1 %9, label %10, label %13

10:                                               ; preds = %2
  %11 = load i32, i32* %3, align 4
  ret i32 %11

12:                                               ; No predecessors!
  br label %16

13:                                               ; preds = %2
  %14 = load i32, i32* %4, align 4
  ret i32 %14

15:                                               ; No predecessors!
  br label %16

16:                                               ; preds = %15, %12
  %17 = load i32, i32* %3, align 4
  ret i32 %17
}

define dso_local i32 @scan() {
  %1 = alloca i32, align 4
  store i32 0, i32* %1, align 4
  %2 = call i32 @getint()
  store i32 %2, i32* %1, align 4
  %3 = load i32, i32* %1, align 4
  %4 = add i32 %3, 0
  ret i32 %4
}

define dso_local void @print(i32 %0) {
  %2 = alloca i32, align 4
  store i32 %0, i32* %2, align 4
  %3 = load i32, i32* %2, align 4
  call void @putint(i32 %3)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.0, i64 0, i64 0))
  ret void
}

define dso_local void @noUse(i32 %0) {
  %2 = alloca i32, align 4
  store i32 %0, i32* %2, align 4
  %3 = alloca i32, align 4
  %4 = load i32, i32* %2, align 4
  store i32 %4, i32* %3, align 4
  ret void
}

define dso_local i32 @mid(i32 %0, i32 %1, i32 %2) {
  %4 = alloca i32, align 4
  store i32 %0, i32* %4, align 4
  %5 = alloca i32, align 4
  store i32 %1, i32* %5, align 4
  %6 = alloca i32, align 4
  store i32 %2, i32* %6, align 4
  %7 = alloca i32, align 4
  store i32 0, i32* %7, align 4
  %8 = load i32, i32* %4, align 4
  %9 = load i32, i32* %5, align 4
  %10 = call i32 @max(i32 %8, i32 %9)
  %11 = load i32, i32* %5, align 4
  %12 = load i32, i32* %6, align 4
  %13 = call i32 @min(i32 %11, i32 %12)
  %14 = icmp eq i32 %10, %13
  br i1 %14, label %15, label %18

15:                                               ; preds = %3
  %16 = load i32, i32* %5, align 4
  ret i32 %16

17:                                               ; No predecessors!
  br label %33

18:                                               ; preds = %3
  %19 = load i32, i32* %4, align 4
  %20 = load i32, i32* %5, align 4
  %21 = call i32 @max(i32 %19, i32 %20)
  %22 = load i32, i32* %4, align 4
  %23 = load i32, i32* %6, align 4
  %24 = call i32 @min(i32 %22, i32 %23)
  %25 = icmp ne i32 %21, %24
  br i1 %25, label %26, label %29

26:                                               ; preds = %18
  %27 = load i32, i32* %6, align 4
  ret i32 %27

28:                                               ; No predecessors!
  br label %32

29:                                               ; preds = %18
  %30 = load i32, i32* %4, align 4
  ret i32 %30

31:                                               ; No predecessors!
  br label %32

32:                                               ; preds = %31, %28
  br label %33

33:                                               ; preds = %32, %17
  %34 = load i32, i32* %5, align 4
  ret i32 %34
}

define dso_local i32 @factorial(i32 %0) {
  %2 = alloca i32, align 4
  store i32 %0, i32* %2, align 4
  %3 = alloca i32, align 4
  %4 = load i32, i32* %2, align 4
  store i32 %4, i32* %3, align 4
  %5 = alloca i32, align 4
  store i32 1, i32* %5, align 4
  %6 = load i32, i32* %2, align 4
  %7 = icmp sgt i32 %6, 20
  %8 = zext i1 %7 to i32
  %9 = icmp ne i32 %8, 0
  br i1 %9, label %10, label %12

10:                                               ; preds = %1
  call void @putstr(i8* getelementptr inbounds ([24 x i8], [24 x i8]* @.str.1, i64 0, i64 0))
  ret i32 -1

11:                                               ; No predecessors!
  br label %12

12:                                               ; preds = %11, %1
  br label %13

13:                                               ; preds = %23, %12
  %14 = load i32, i32* %3, align 4
  %15 = icmp ne i32 %14, 0
  br i1 %15, label %16, label %24

16:                                               ; preds = %13
  call void @putstr(i8* getelementptr inbounds ([5 x i8], [5 x i8]* @.str.2, i64 0, i64 0))
  %17 = load i32, i32* %5, align 4
  call void @putint(i32 %17)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.3, i64 0, i64 0))
  %18 = load i32, i32* %5, align 4
  %19 = load i32, i32* %3, align 4
  %20 = mul i32 %18, %19
  store i32 %20, i32* %5, align 4
  %21 = load i32, i32* %3, align 4
  %22 = sub i32 %21, 1
  store i32 %22, i32* %3, align 4
  br label %23

23:                                               ; preds = %16
  br label %13

24:                                               ; preds = %13
  %25 = load i32, i32* %5, align 4
  ret i32 %25
}

define dso_local i32 @main() {
  %1 = alloca i32, align 4
  %2 = load i32, i32* @q, align 4
  %3 = call i32 @min(i32 88, i32 %2)
  %4 = load i32, i32* @s, align 4
  %5 = call i32 @scan()
  %6 = call i32 @max(i32 %4, i32 %5)
  %7 = call i32 @max(i32 %3, i32 %6)
  store i32 %7, i32* %1, align 4
  %8 = alloca i32, align 4
  %9 = load i32, i32* @r, align 4
  %10 = call i32 @scan()
  %11 = call i32 @min(i32 %9, i32 %10)
  store i32 %11, i32* %8, align 4
  %12 = alloca i32, align 4
  store i32 58, i32* %12, align 4
  %13 = alloca i32, align 4
  store i32 65535, i32* %13, align 4
  %14 = alloca i32, align 4
  store i32 0, i32* %14, align 4
  %15 = alloca i32, align 4
  store i32 1, i32* %15, align 4
  %16 = alloca i32, align 4
  store i32 -1, i32* %16, align 4
  %17 = alloca i32, align 4
  store i32 -10, i32* %17, align 4
  call void @putstr(i8* getelementptr inbounds ([10 x i8], [10 x i8]* @.str.4, i64 0, i64 0))
  %18 = load i32, i32* %8, align 4
  %19 = load i32, i32* %12, align 4
  %20 = add i32 %18, %19
  %21 = load i32, i32* %1, align 4
  %22 = sub i32 %20, %21
  store i32 %22, i32* %8, align 4
  call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.str.5, i64 0, i64 0))
  %23 = load i32, i32* %8, align 4
  call void @putint(i32 %23)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.6, i64 0, i64 0))
  %24 = load i32, i32* %15, align 4
  %25 = load i32, i32* %16, align 4
  %26 = add i32 %24, %25
  %27 = load i32, i32* %14, align 4
  %28 = load i32, i32* %14, align 4
  %29 = icmp eq i32 0, %28
  %30 = zext i1 %29 to i32
  %31 = icmp ne i32 %30, 0
  br i1 %31, label %32, label %33

32:                                               ; preds = %0
  call void @putstr(i8* getelementptr inbounds ([17 x i8], [17 x i8]* @.str.7, i64 0, i64 0))
  br label %33

33:                                               ; preds = %32, %0
  %34 = alloca i32, align 4
  store i32 0, i32* %34, align 4
  %35 = call i32 @scan()
  store i32 %35, i32* %34, align 4
  %36 = load i32, i32* %34, align 4
  %37 = add i32 %36, 10
  call void @print(i32 %37)
  %38 = load i32, i32* %34, align 4
  store i32 %38, i32* %12, align 4
  %39 = call i32 @scan()
  store i32 %39, i32* %1, align 4
  %40 = call i32 @scan()
  store i32 %40, i32* %8, align 4
  %41 = call i32 @scan()
  store i32 %41, i32* %12, align 4
  %42 = load i32, i32* %1, align 4
  %43 = load i32, i32* %8, align 4
  %44 = load i32, i32* %12, align 4
  %45 = call i32 @mid(i32 %42, i32 %43, i32 %44)
  %46 = load i32, i32* %1, align 4
  %47 = icmp sle i32 %45, %46
  %48 = zext i1 %47 to i32
  %49 = icmp ne i32 %48, 0
  br i1 %49, label %50, label %65

50:                                               ; preds = %33
  call void @putstr(i8* getelementptr inbounds ([14 x i8], [14 x i8]* @.str.8, i64 0, i64 0))
  %51 = load i32, i32* %1, align 4
  %52 = load i32, i32* %1, align 4
  %53 = load i32, i32* %8, align 4
  %54 = load i32, i32* %12, align 4
  %55 = call i32 @mid(i32 %52, i32 %53, i32 %54)
  %56 = sdiv i32 %55, 6
  %57 = load i32, i32* %12, align 4
  %58 = mul i32 %56, %57
  %59 = srem i32 %58, 2
  %60 = add i32 %51, %59
  %61 = load i32, i32* %14, align 4
  %62 = load i32, i32* %16, align 4
  %63 = mul i32 %61, %62
  %64 = sub i32 %60, %63
  call void @putint(i32 %64)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.9, i64 0, i64 0))
  br label %87

65:                                               ; preds = %33
  %66 = load i32, i32* %1, align 4
  %67 = load i32, i32* %8, align 4
  %68 = load i32, i32* %12, align 4
  %69 = call i32 @mid(i32 %66, i32 %67, i32 %68)
  %70 = load i32, i32* %12, align 4
  %71 = icmp slt i32 %69, %70
  %72 = zext i1 %71 to i32
  %73 = icmp ne i32 %72, 0
  br i1 %73, label %74, label %75

74:                                               ; preds = %65
  call void @putstr(i8* getelementptr inbounds ([6 x i8], [6 x i8]* @.str.10, i64 0, i64 0))
  br label %86

75:                                               ; preds = %65
  call void @putstr(i8* getelementptr inbounds ([8 x i8], [8 x i8]* @.str.11, i64 0, i64 0))
  %76 = load i32, i32* %1, align 4
  %77 = load i32, i32* %8, align 4
  %78 = load i32, i32* %12, align 4
  %79 = call i32 @mid(i32 %76, i32 %77, i32 %78)
  call void @putint(i32 %79)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.12, i64 0, i64 0))
  %80 = load i32, i32* %1, align 4
  %81 = load i32, i32* %8, align 4
  %82 = load i32, i32* %12, align 4
  %83 = call i32 @mid(i32 %80, i32 %81, i32 %82)
  %84 = srem i32 %83, 65535
  %85 = call i32 @factorial(i32 %84)
  call void @putint(i32 %85)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.13, i64 0, i64 0))
  br label %86

86:                                               ; preds = %75, %74
  br label %87

87:                                               ; preds = %86, %50
  %88 = load i32, i32* %1, align 4
  call void @noUse(i32 %88)
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
