DEFAULT REL

; import the required external symbols for the system call functions
extern _read
extern _write
extern _exit

; export entry point
global _start

LENGTH equ 64
section .bss
alignb 8
BUFFER resb LENGTH

section .data
INV_IN db "Invalid input!", 0
INV_IN_LEN equ $ - INV_IN


section .text

_toInt:
    .LOOP:
    movzx rdx, byte [BUFFER + rbx]  ;read byte from buffer
    sub rdx, '0'                    ;convert to integer
    imul rax, rax, 10
    add rax, rdx                    ;add to result
    inc rbx

    cmp rbx, rcx                    ;compare counter to length
    jne .LOOP

    ret

_toStr:
    mov rbx, 10
    ;mov r8, 0                       ;counter

    .LOOP:
    mov rdx, 0
    idiv rbx                        ;divide by ten
    add rdx, '0'                    ;convert rest of division to string
    mov [BUFFER + r8], dl           ;write to buffer
    inc r8

    cmp rax, 0
    jne .LOOP

    mov rax, r8                     ;store length in rax
    cmp rax, 1                      ; if length == 1 we don't need to reverse the buffer content
    jbe .DONE

    mov r9, r10
    sub r8, 1                       ;subtract one to get position of last character;

    .LOOP1:
    movzx rbx, byte [BUFFER + r8]   ;load last char
    movzx rcx, byte [BUFFER + r9]   ;load first char
    mov [BUFFER + r8], cl           ;write first char to last position
    mov [BUFFER + r9], bl           ;write last char to first position
    dec r8                          ;decrement end position
    inc r9                          ;increment start position
    cmp r9, r8
    jae .DONE                       ;if r9 >= r8 we're done
    jmp .LOOP1                      ;else we run the loop again

    .DONE:
    ret

_validateInput:     ;returns result in rax. if rax contains -1 the input is invalid
                    ;if rax contains 0 the input is positive, if it contains 1 the input is negative
    cmp rcx, 0
    je .INVALID                 ;if length == 0 input is technically invalid

    movzx rdx, byte [BUFFER]
    cmp rdx, '-'                ;if the first char is '-' the number is negative
    jne .P
    cmp rcx, 1                  ;if the number is negative, but '-' is the only char, the input is invalid
    jl .INVALID
    mov rax, 1                  ;1 indicates a negative number
    mov rbx, 1                  ;start of number is at index 1

    .LOOP:
    movzx rdx, byte [BUFFER + rbx]  ;read byte from buffer
    sub rdx, '0'                    ;convert to integer
    cmp rdx, 0                      ;if integer < 0 it is invalid
    jl .INVALID
    cmp rdx, 9                      ;if integer is >9 it is invalid
    jg .INVALID
    inc rbx                         ;increment counter
    cmp rbx, rcx
    je .DONE
    jmp .LOOP

    .P:
    mov rax, 0                  ;0 indicates a positive number
    mov rbx, 0                  ;start of the number is at index 0
    jmp .LOOP

    .INVALID:
    mov rax, -1                  ;-1 indicates an invalid input

    .DONE:
    ret

_start:
            push  rbp                   ; store pointer to previous frame, and additionally
                                        ; ensure that the stack is aligned for the subsequent
                                        ; function calls. Required for Windows and MacOS.

            mov rsi, LENGTH
            mov rdi, BUFFER

            call _read                  ;rax contains length of input + \0\n
            mov rcx, rax
            sub rcx, 1                  ;remove \0
            call _validateInput
            cmp rax, -1
            je .INVALID_INPUT


            mov rbx, rax                ;store the start index of the number in rbx
            mov r10, rax                ;store sign indicator in r10
            mov rax, 0
            call _toInt


            ;division
            mov rbx, 2
            mov rdx, 0
            idiv rbx

            mov r8, r10
            call _toStr

            mov rdi, BUFFER
            mov rsi, rax
            call _write
            jmp .END

            .INVALID_INPUT:
            mov rdi, INV_IN
            mov rsi, INV_IN_LEN
            call _write

            .END:
            call exit
; implement divider (milestone 1)

; exit program with exit code 0
exit:       mov   rdi, 0                ; first parameter: set exit code
            call  _exit                 ; call function