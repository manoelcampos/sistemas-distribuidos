#include <stdio.h>
#include <stdlib.h>

#define FILE_NAME "integer.bin"

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
 * Lê um arquivo binário contendo um número inteiro,
 * que pode ter sido gerado em C ou Java.
 * Dependendo do ByteOrder usado no arquivo gerado
 * a partir do Java, o programa em C não lê o arquivo corretamente.
*/
int main(){
    printf("Lido valor %d do arquivo\n", readFile());
    return 0;
}