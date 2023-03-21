#include <stdio.h>
#include <stdlib.h>

#define FILE_NAME "integer.bin"

void writeFile(int value){
    FILE* arq;
    if((arq = fopen(FILE_NAME, "wb+")) != NULL){
        printf("Criado arquivo %s\n", FILE_NAME);
        fwrite(&value, sizeof(int), 1, arq);
        printf("Gravado o valor %d no arquivo\n\n", value);
        fclose(arq);
    }
}

int readFile(){
    FILE* arq;
    int value = -1;
    if((arq = fopen(FILE_NAME, "rb")) != NULL){
        printf("Aberto arquivo %s\n", FILE_NAME);
        fread(&value, sizeof(int), 1, arq);
        fclose(arq);
    }

    return value;
}

/**
 * Cria um arquivo binário contendo um número inteiro,
 * para tentar ser lido em Java ou em outro programa em C.
 * Se a aplicação Java não definir o ByteOrder como LITTLE_ENDIAN,
 * o arquivo não é lido corretamente (pois este é o ByteOrder padrão 
 * na linguagem C).
*/
int main(){
    int value = 135;
    writeFile(value);
    printf("Lido valor %d do arquivo\n", readFile());
    return 0;
}