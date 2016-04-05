/**
 * Calcula a moda
 */
int getMode(int* values, int size) {
    int maior = 0;
    int valorAtual = values[0];
    int quantidadeMax = 0;
    int quantidadeAtual = 0;
    int i = 0;
    for(; i<size; i++){
        if (values[i] == valorAtual) {
            quantidadeAtual++;
        }
        else {
            valorAtual = values[i];
            quantidadeAtual = 1;
        }

        if (quantidadeAtual > quantidadeMax) {
            quantidadeMax = quantidadeAtual;
            maior = values[i];
        }
    }
    return maior;
}

int getLessRepeated(int* values, int size) {
    //FIXME: Fazer funcionar isso aqui.
    int menor = 0;
    int valorAtual = values[0];
    int quantidadeMin = 0;
    int quantidadeAtual = 1;
    int i = 0;
    for(; i<size; i++){
        if (values[i] == valorAtual) {
            quantidadeAtual++;
        }
        else {
            valorAtual = values[i];
            quantidadeAtual = 1;
        }

        if (quantidadeAtual < quantidadeMin) {
            quantidadeMin = quantidadeAtual;
            menor = values[i];
        }
    }
    return menor;
}