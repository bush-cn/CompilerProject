; ModuleID = 'llvm-link'
source_filename = "llvm-link"
target datalayout = "e-m:e-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-linux-gnu"

@a = dso_local constant i32 10
@ty = dso_local constant i32 90
@b = dso_local constant [3 x i32] [i32 1, i32 2, i32 3]
@x = dso_local global i32 5
@z = dso_local global i32 114514
@y = dso_local global [3 x i32] zeroinitializer
@global_var = dso_local global i32 0
@buaa = dso_local global i8 92
@aa = dso_local constant [5 x i8] c"abc\\0"
@aaa = dso_local global [6 x i8] c"xyz\22\00\00"
@.str.0 = private unnamed_addr constant [10 x i8] c"21374067\0A\00", align 1
@.str.1 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.2 = private unnamed_addr constant [4 x i8] c"i: \00", align 1
@.str.3 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.4 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.5 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str = private unnamed_addr constant [3 x i8] c"%c\00", align 1
@.str.1.5 = private unnamed_addr constant [3 x i8] c"%d\00", align 1
@.str.2.10 = private unnamed_addr constant [4 x i8] c"%d:\00", align 1
@.str.3.11 = private unnamed_addr constant [4 x i8] c" %d\00", align 1
@.str.4.12 = private unnamed_addr constant [2 x i8] c"\0A\00", align 1
@.str.5.15 = private unnamed_addr constant [3 x i8] c"%s\00", align 1

define dso_local i32 @g(i32* %0) {
  %2 = getelementptr i32, i32* %0, i32 0
  %3 = load i32, i32* %2, align 4
  %4 = getelementptr i32, i32* %0, i32 1
  %5 = load i32, i32* %4, align 4
  %6 = getelementptr i32, i32* %0, i32 0
  %7 = load i32, i32* %6, align 4
  %8 = sub i32 0, %7
  %9 = add i32 %5, %8
  %10 = getelementptr i32, i32* %0, i32 %9
  %11 = load i32, i32* %10, align 4
  %12 = add i32 %3, %11
  ret i32 %12
}

define dso_local i8 @foo(i32 %0, i32 %1) {
  %3 = alloca i32, align 4
  store i32 %0, i32* %3, align 4
  %4 = alloca i32, align 4
  store i32 %1, i32* %4, align 4
  %5 = trunc i32 111 to i8
  ret i8 %5
}

define dso_local void @fooo(i32 %0, i32 %1) {
  %3 = alloca i32, align 4
  store i32 %0, i32* %3, align 4
  %4 = alloca i32, align 4
  store i32 %1, i32* %4, align 4
  ret void
}

define dso_local i32 @func() {
  %1 = load i32, i32* @global_var, align 4
  %2 = add i32 %1, 1
  store i32 %2, i32* @global_var, align 4
  ret i32 1
}

define dso_local i32 @main() {
  call void @putstr(i8* getelementptr inbounds ([10 x i8], [10 x i8]* @.str.0, i64 0, i64 0))
  %1 = alloca i32, align 4
  store i32 0, i32* %1, align 4
  %2 = alloca i8, align 1
  %3 = trunc i32 97 to i8
  store i8 %3, i8* %2, align 1
  %4 = alloca i8, align 1
  store i8 98, i8* %4, align 1
  %5 = alloca i32, align 4
  store i32 -10, i32* %5, align 4
  %6 = alloca i32, align 4
  %7 = load i32, i32* %5, align 4
  %8 = add i32 %7, 5
  %9 = mul i32 %8, 2
  %10 = sdiv i32 %9, 1
  %11 = add i32 %10, 0
  store i32 %11, i32* %6, align 4
  %12 = load i32, i32* %5, align 4
  %13 = icmp slt i32 %12, 20
  %14 = zext i1 %13 to i32
  %15 = icmp ne i32 %14, 0
  br i1 %15, label %21, label %16

16:                                               ; preds = %0
  %17 = call i32 @func()
  %18 = icmp eq i32 0, %17
  %19 = zext i1 %18 to i32
  %20 = icmp ne i32 %19, 0
  br i1 %20, label %21, label %24

21:                                               ; preds = %16, %0
  %22 = load i32, i32* %5, align 4
  %23 = sub i32 %22, 1
  store i32 %23, i32* %5, align 4
  br label %38

24:                                               ; preds = %16
  %25 = load i32, i32* %5, align 4
  %26 = icmp sgt i32 %25, 0
  %27 = zext i1 %26 to i32
  %28 = icmp ne i32 %27, 0
  br i1 %28, label %29, label %37

29:                                               ; preds = %24
  %30 = call i32 @func()
  %31 = icmp ne i32 %30, 0
  br i1 %31, label %32, label %37

32:                                               ; preds = %29
  %33 = load i32, i32* %5, align 4
  %34 = add i32 %33, 1
  store i32 %34, i32* %5, align 4
  %35 = load i32, i32* %5, align 4
  %36 = add i32 %35, 1
  br label %37

37:                                               ; preds = %32, %29, %24
  br label %38

38:                                               ; preds = %37, %21
  %39 = load i32, i32* %5, align 4
  call void @putint(i32 %39)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.1, i64 0, i64 0))
  store i32 0, i32* %1, align 4
  br label %40

40:                                               ; preds = %48, %38
  %41 = load i32, i32* %1, align 4
  %42 = icmp slt i32 %41, 6
  %43 = zext i1 %42 to i32
  %44 = icmp ne i32 %43, 0
  br i1 %44, label %45, label %51

45:                                               ; preds = %40
  call void @putstr(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.2, i64 0, i64 0))
  %46 = load i32, i32* %1, align 4
  call void @putint(i32 %46)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.3, i64 0, i64 0))
  br label %48

47:                                               ; No predecessors!
  br label %48

48:                                               ; preds = %47, %45
  %49 = load i32, i32* %1, align 4
  %50 = add i32 %49, 1
  store i32 %50, i32* %1, align 4
  br label %40

51:                                               ; preds = %40
  %52 = alloca [3 x i32], align 4
  %53 = getelementptr [3 x i32], [3 x i32]* %52, i32 0, i32 0
  store i32 1, i32* %53, align 4
  %54 = getelementptr [3 x i32], [3 x i32]* %52, i32 0, i32 1
  store i32 2, i32* %54, align 4
  %55 = getelementptr [3 x i32], [3 x i32]* %52, i32 0, i32 2
  store i32 3, i32* %55, align 4
  %56 = alloca i32, align 4
  %57 = call i32 @func()
  store i32 %57, i32* %56, align 4
  %58 = alloca i32, align 4
  %59 = call i32 @getint()
  store i32 %59, i32* %58, align 4
  %60 = getelementptr [3 x i32], [3 x i32]* %52, i32 0, i32 0
  %61 = call i32 @g(i32* %60)
  store i32 %61, i32* %1, align 4
  %62 = call i32 @getchar()
  %63 = trunc i32 %62 to i8
  store i8 %63, i8* %2, align 1
  %64 = trunc i32 97 to i8
  store i8 %64, i8* %2, align 1
  br label %65

65:                                               ; preds = %85, %51
  %66 = load i8, i8* %2, align 1
  %67 = zext i8 %66 to i32
  %68 = icmp slt i32 %67, 127
  %69 = zext i1 %68 to i32
  %70 = icmp ne i32 %69, 0
  br i1 %70, label %74, label %71

71:                                               ; preds = %65
  %72 = load i32, i32* %1, align 4
  %73 = icmp ne i32 %72, 0
  br i1 %73, label %74, label %90

74:                                               ; preds = %71, %65
  %75 = load i8, i8* %2, align 1
  %76 = zext i8 %75 to i32
  %77 = add i32 %76, 1
  %78 = trunc i32 %77 to i8
  store i8 %78, i8* %2, align 1
  %79 = load i8, i8* %2, align 1
  %80 = zext i8 %79 to i32
  %81 = icmp eq i32 %80, 120
  br i1 %81, label %82, label %84

82:                                               ; preds = %74
  br label %90

83:                                               ; No predecessors!
  br label %84

84:                                               ; preds = %83, %74
  br label %85

85:                                               ; preds = %84
  %86 = load i8, i8* %2, align 1
  %87 = zext i8 %86 to i32
  %88 = add i32 %87, 1
  %89 = trunc i32 %88 to i8
  store i8 %89, i8* %2, align 1
  br label %65

90:                                               ; preds = %82, %71
  %91 = trunc i32 97 to i8
  store i8 %91, i8* %2, align 1
  br label %92

92:                                               ; preds = %104, %90
  br label %93

93:                                               ; preds = %92
  %94 = load i8, i8* %2, align 1
  %95 = zext i8 %94 to i32
  %96 = add i32 %95, 1
  %97 = trunc i32 %96 to i8
  store i8 %97, i8* %2, align 1
  %98 = load i8, i8* %2, align 1
  %99 = zext i8 %98 to i32
  %100 = icmp eq i32 %99, 120
  br i1 %100, label %101, label %103

101:                                              ; preds = %93
  br label %109

102:                                              ; No predecessors!
  br label %103

103:                                              ; preds = %102, %93
  br label %104

104:                                              ; preds = %103
  %105 = load i8, i8* %2, align 1
  %106 = zext i8 %105 to i32
  %107 = add i32 %106, 1
  %108 = trunc i32 %107 to i8
  store i8 %108, i8* %2, align 1
  br label %92

109:                                              ; preds = %101
  %110 = load i8, i8* %2, align 1
  %111 = zext i8 %110 to i32
  call void @putch(i32 %111)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.4, i64 0, i64 0))
  %112 = load i8, i8* %2, align 1
  %113 = zext i8 %112 to i32
  call void @putint(i32 %113)
  call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @.str.5, i64 0, i64 0))
  %114 = load i8, i8* %2, align 1
  %115 = zext i8 %114 to i32
  %116 = icmp sgt i32 %115, 0
  %117 = zext i1 %116 to i32
  %118 = icmp ne i32 %117, 0
  br i1 %118, label %119, label %237

119:                                              ; preds = %109
  %120 = load i8, i8* %2, align 1
  %121 = zext i8 %120 to i32
  %122 = icmp slt i32 %121, 0
  %123 = zext i1 %122 to i32
  %124 = icmp ne i32 %123, 0
  br i1 %124, label %125, label %237

125:                                              ; preds = %119
  %126 = load i8, i8* %2, align 1
  %127 = zext i8 %126 to i32
  %128 = icmp sle i32 %127, 0
  %129 = zext i1 %128 to i32
  %130 = icmp ne i32 %129, 0
  br i1 %130, label %131, label %237

131:                                              ; preds = %125
  %132 = load i8, i8* %2, align 1
  %133 = zext i8 %132 to i32
  %134 = icmp sge i32 %133, 0
  %135 = zext i1 %134 to i32
  %136 = icmp ne i32 %135, 0
  br i1 %136, label %137, label %237

137:                                              ; preds = %131
  %138 = load i8, i8* %2, align 1
  %139 = zext i8 %138 to i32
  %140 = icmp ne i32 %139, 0
  br i1 %140, label %141, label %237

141:                                              ; preds = %137
  %142 = load i8, i8* %2, align 1
  %143 = zext i8 %142 to i32
  %144 = icmp eq i32 %143, 0
  br i1 %144, label %145, label %237

145:                                              ; preds = %141
  br label %146

146:                                              ; preds = %163, %145
  %147 = load i8, i8* %2, align 1
  %148 = zext i8 %147 to i32
  %149 = icmp eq i32 0, %148
  %150 = zext i1 %149 to i32
  %151 = icmp ne i32 %150, 0
  br i1 %151, label %152, label %168

152:                                              ; preds = %146
  %153 = load i8, i8* %2, align 1
  %154 = zext i8 %153 to i32
  %155 = add i32 %154, 1
  %156 = trunc i32 %155 to i8
  store i8 %156, i8* %2, align 1
  %157 = load i8, i8* %2, align 1
  %158 = zext i8 %157 to i32
  %159 = icmp eq i32 %158, 120
  br i1 %159, label %160, label %162

160:                                              ; preds = %152
  br label %168

161:                                              ; No predecessors!
  br label %162

162:                                              ; preds = %161, %152
  br label %163

163:                                              ; preds = %162
  %164 = load i8, i8* %2, align 1
  %165 = zext i8 %164 to i32
  %166 = add i32 %165, 1
  %167 = trunc i32 %166 to i8
  store i8 %167, i8* %2, align 1
  br label %146

168:                                              ; preds = %160, %146
  br label %169

169:                                              ; preds = %172, %168
  br label %170

170:                                              ; preds = %169
  %171 = trunc i32 97 to i8
  store i8 %171, i8* %2, align 1
  br label %172

172:                                              ; preds = %170
  br label %169

173:                                              ; No predecessors!
  %174 = load i8, i8* %2, align 1
  %175 = zext i8 %174 to i32
  %176 = trunc i32 %175 to i8
  store i8 %176, i8* %2, align 1
  br label %177

177:                                              ; preds = %180, %173
  br label %178

178:                                              ; preds = %177
  %179 = trunc i32 98 to i8
  store i8 %179, i8* %2, align 1
  br label %180

180:                                              ; preds = %178
  br label %177

181:                                              ; No predecessors!
  br label %182

182:                                              ; preds = %185, %181
  br label %183

183:                                              ; preds = %182
  %184 = trunc i32 99 to i8
  store i8 %184, i8* %2, align 1
  br label %185

185:                                              ; preds = %183
  %186 = load i8, i8* %2, align 1
  %187 = zext i8 %186 to i32
  %188 = trunc i32 %187 to i8
  store i8 %188, i8* %2, align 1
  br label %182

189:                                              ; No predecessors!
  br label %190

190:                                              ; preds = %198, %189
  %191 = load i8, i8* %2, align 1
  %192 = zext i8 %191 to i32
  %193 = load i8, i8* %2, align 1
  %194 = zext i8 %193 to i32
  %195 = icmp ne i32 %192, %194
  br i1 %195, label %196, label %199

196:                                              ; preds = %190
  %197 = trunc i32 100 to i8
  store i8 %197, i8* %2, align 1
  br label %198

198:                                              ; preds = %196
  br label %190

199:                                              ; preds = %190
  %200 = load i8, i8* %2, align 1
  %201 = zext i8 %200 to i32
  %202 = trunc i32 %201 to i8
  store i8 %202, i8* %2, align 1
  br label %203

203:                                              ; preds = %211, %199
  %204 = load i8, i8* %2, align 1
  %205 = zext i8 %204 to i32
  %206 = load i8, i8* %2, align 1
  %207 = zext i8 %206 to i32
  %208 = icmp ne i32 %205, %207
  br i1 %208, label %209, label %212

209:                                              ; preds = %203
  %210 = trunc i32 101 to i8
  store i8 %210, i8* %2, align 1
  br label %211

211:                                              ; preds = %209
  br label %203

212:                                              ; preds = %203
  %213 = load i8, i8* %2, align 1
  %214 = zext i8 %213 to i32
  %215 = trunc i32 %214 to i8
  store i8 %215, i8* %2, align 1
  br label %216

216:                                              ; preds = %219, %212
  br label %217

217:                                              ; preds = %216
  %218 = trunc i32 102 to i8
  store i8 %218, i8* %2, align 1
  br label %219

219:                                              ; preds = %217
  %220 = load i8, i8* %2, align 1
  %221 = zext i8 %220 to i32
  %222 = trunc i32 %221 to i8
  store i8 %222, i8* %2, align 1
  br label %216

223:                                              ; No predecessors!
  br label %224

224:                                              ; preds = %232, %223
  %225 = load i8, i8* %2, align 1
  %226 = zext i8 %225 to i32
  %227 = load i8, i8* %2, align 1
  %228 = zext i8 %227 to i32
  %229 = icmp ne i32 %226, %228
  br i1 %229, label %230, label %236

230:                                              ; preds = %224
  %231 = trunc i32 103 to i8
  store i8 %231, i8* %2, align 1
  br label %232

232:                                              ; preds = %230
  %233 = load i8, i8* %2, align 1
  %234 = zext i8 %233 to i32
  %235 = trunc i32 %234 to i8
  store i8 %235, i8* %2, align 1
  br label %224

236:                                              ; preds = %224
  br label %237

237:                                              ; preds = %236, %141, %137, %131, %125, %119, %109
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
  %2 = call i32 (i8*, ...) @__isoc99_scanf(i8* noundef getelementptr inbounds ([3 x i8], [3 x i8]* @.str.1.5, i64 0, i64 0), i32* noundef %1)
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
  %5 = call i32 (i8*, ...) @__isoc99_scanf(i8* noundef getelementptr inbounds ([3 x i8], [3 x i8]* @.str.1.5, i64 0, i64 0), i32* noundef %3)
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
  %15 = call i32 (i8*, ...) @__isoc99_scanf(i8* noundef getelementptr inbounds ([3 x i8], [3 x i8]* @.str.1.5, i64 0, i64 0), i32* noundef %14)
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
  %4 = call i32 (i8*, ...) @printf(i8* noundef getelementptr inbounds ([3 x i8], [3 x i8]* @.str.1.5, i64 0, i64 0), i32 noundef %3)
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
  %7 = call i32 (i8*, ...) @printf(i8* noundef getelementptr inbounds ([4 x i8], [4 x i8]* @.str.2.10, i64 0, i64 0), i32 noundef %6)
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
  %18 = call i32 (i8*, ...) @printf(i8* noundef getelementptr inbounds ([4 x i8], [4 x i8]* @.str.3.11, i64 0, i64 0), i32 noundef %17)
  br label %19

19:                                               ; preds = %12
  %20 = load i32, i32* %5, align 4
  %21 = add nsw i32 %20, 1
  store i32 %21, i32* %5, align 4
  br label %8, !llvm.loop !9

22:                                               ; preds = %8
  %23 = call i32 (i8*, ...) @printf(i8* noundef getelementptr inbounds ([2 x i8], [2 x i8]* @.str.4.12, i64 0, i64 0))
  ret void
}

; Function Attrs: noinline nounwind optnone uwtable
define dso_local void @putstr(i8* noundef %0) #0 {
  %2 = alloca i8*, align 8
  store i8* %0, i8** %2, align 8
  %3 = load i8*, i8** %2, align 8
  %4 = call i32 (i8*, ...) @printf(i8* noundef getelementptr inbounds ([3 x i8], [3 x i8]* @.str.5.15, i64 0, i64 0), i8* noundef %3)
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
