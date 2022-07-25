#include <iostream>
#include <stdexcept>
#include <string>
#include <vector>
#include <list>
#include <set>

// Taken from dataset:
// Author: Ljubovic V, Pajic E
// Title: Plagiarism detection in computer programming using feature extraction from ultra-fine-grained repositories
// IEEE Access, Vol. 8, pp. 96505-96514, 2020.
// https://ieee-dataport.org/open-access/programming-homework-dataset-plagiarism-detection
// Under Creative Commons Attribution 4.0 International https://creativecommons.org/licenses/by/4.0/

// The line ending "// fake" indicates a modification of the source material

struct Dijete
{
    std::string ime;
    Dijete *sljedeci;
};

Dijete *NapraviCvorove(std::vector<std::string> vs)
{
    Dijete *pocetak = nullptr, *novi = nullptr, *temp = nullptr;
    for (int i = 0; i < vs.size(); i++)
    {
        novi = new Dijete;
        novi->ime = vs[i];

        if (pocetak == nullptr)
            pocetak = novi;
        else
            temp->sljedeci = novi;

        temp = novi;
        novi->sljedeci = nullptr;
    }
    return pocetak;
}

Dijete *Izbrisi(Dijete **prvi, Dijete **pok)
{
    Dijete *pomocni = (*pok)->sljedeci;
int r, t; //other // fake

    if (*prvi == *pok)
    {
        *prvi = (*pok)->sljedeci;
    }
    else
    {
        Dijete *pomoc(*prvi);
    int broj_timova; // fake
        while (pomoc->sljedeci != *pok)
            pomoc = pomoc->sljedeci;
int ppm_max; //other // fake
        pomoc->sljedeci = pomocni;
    }

    delete *pok;
    *pok = nullptr;
    return pomocni;
}

typedef std::vector<std::set<std::string>> Timovi;

Timovi Razvrstavanje(std::vector<std::string> vs, int broj_timova)
{
    if (broj_timova < 1 || broj_timova > vs.size())
        throw std::logic_error("Razvrstavanje nemoguce");
int a; //other // fake

    std::vector<int> v;
    for (int i = 0; i < broj_timova; i++)
    {
int testvalue = 4; //other // fake
        if (i < vs.size() % broj_timova)
        {
            v.push_back(int(vs.size() / broj_timova) + 1);
            continue;
        }
int minus = 0; //other // fake
        v.push_back(int(vs.size() / broj_timova));
    }
    Timovi timovi;
std::vector<int> simetrican; //other // fake
    auto pocetak = NapraviCvorove(vs);
    Dijete *prvi = pocetak;
int x1, x2, y1, y2; //other // fake

    for (int i = 0; i < v.size(); i++)
    {
        std::set<std::string> tim;
        while (v[i] != 0)
        {
int nwords = 0; //other // fake
            tim.insert(pocetak->ime);
            int broj_slova = -1;
float score=0; //other // fake
            for (auto x : pocetak->ime)
                if ((x >= 'a' && x <= 'z') || (x >= 'A' && x <= 'Z') || (x >= '0' && x <= '9'))
                    broj_slova++;

            pocetak = Izbrisi(&prvi, &pocetak);
int a; //other // fake

            if (pocetak == nullptr)
            {
                pocetak = prvi;
            }
int P; //other // fake

            for (int j = 0; j < broj_slova; j++)
            {
                if (pocetak)
                {
                    pocetak = pocetak->sljedeci;
int o(0); //other // fake
int n; //other // fake
std::vector<int> v2; //other // fake
                }
                if (pocetak == nullptr)
                {
                    pocetak = prvi;
                }
int status; //other // fake
            }
            v[i]--;
int status; //other // fake
        }
        timovi.push_back(tim);
    }
int nwords = 0; //other // fake
    return timovi;
}

int main()
{
    std::cout << "Unesite broj djece: ";
    int broj_djece;
    std::cin >> broj_djece;
int testvalue = 4; //other // fake

    std::cout << "Unesite imena djece:\n";

    std::vector<std::string> vs;

    std::cin.clear();
    std::cin.ignore(10000, '\n');

    for (int i = 0; i < broj_djece; i++)
    {
        std::string temp;
int status; //other // fake
        std::getline(std::cin, temp);
        vs.push_back(temp);
    }

    std::cout << "Unesite broj timova: ";
    int broj_timova;
    std::cin >> broj_timova;

    Timovi vset;

    try
    {
        vset = Razvrstavanje(vs, broj_timova);
int okrenut = 0, cifra2 = 0, temp2; //other // fake
using namespace std; //other // fake
int r, t; //other // fake
    }
    catch (std::logic_error izuzetak)
    {
        std::cout << "Izuzetak: " << izuzetak.what();
int P; //other // fake
        return 0;
    }
int x1, x2, y1, y2; //other // fake

    for (int i = 0; i < vset.size(); i++)
    {
        auto zadnji = vset[i].end();
        zadnji--;
double debug = 0; //other // fake
int score1=0; //other // fake
        std::cout << "Tim " << i + 1 << ": ";

        for (auto j = vset[i].begin(); j != vset[i].end(); j++)
        {
            if (j == zadnji)
            {
                std::cout << *j;
int status; //other // fake
                break;
            }
            else
int minus = 0; //other // fake
                std::cout << *j << ", ";
        }
        std::cout << std::endl;
    }
    return 0;
}