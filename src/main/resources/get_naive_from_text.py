import json
import os.path
import nltk.data
import logging
import sys
# reload(sys)
# sys.setdefaultencoding('utf8')


def run_select(input, output, substring):
# inp = "./data/wiki.small-01-p10p30302.en.text"
# outp = "born-large-sents.txt"

    tokenizer = nltk.data.load('tokenizers/punkt/english.pickle')
    fp = open(input)
    data = fp.read()
    sents = tokenizer.tokenize(data)
    print(input, output, substring)
    for sentence in sents:
        if substring in sentence:
                print(sentence, '\n')
                with open(output, 'a+') as outfile:
                    outfile.write(sentence+"\n")


if __name__ == '__main__':
    program = os.path.basename(sys.argv[0])
    logger = logging.getLogger(program)

    logging.basicConfig(format='%(asctime)s: %(levelname)s: %(message)s')
    logging.root.setLevel(level=logging.INFO)
    logger.info("running %s" % ' '.join(sys.argv))

    # check and process input arguments
    if len(sys.argv) != 4:
        print("Using: python get_naive_from_text.py <input> <output> <substring>")
        sys.exit(1)
    input, output, substring = sys.argv[1:4]
    run_select(input, output, substring)
