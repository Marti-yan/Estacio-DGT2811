"""Teste HTTP contra uma instalação local; cria e remove somente seus produtos."""
import html
import http.cookiejar
import re
import sys
import urllib.error
import urllib.parse
import urllib.request
import uuid

base = (sys.argv[1] if len(sys.argv) > 1 else 'http://localhost:8080/CadastroEE-war').rstrip('/')
url = base + '/ServletProdutoFC'
client = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
checks = 0
created = []


def request(params, method='GET'):
    encoded = urllib.parse.urlencode(params)
    target = url + '?' + encoded if method == 'GET' else url
    data = encoded.encode() if method == 'POST' else None
    try:
        response = client.open(target, data, timeout=30)
    except urllib.error.HTTPError as error:
        response = error
    return response.status, response.read().decode('utf-8')


def check(condition, message):
    global checks
    if not condition:
        raise AssertionError(message)
    checks += 1
    print('OK - ' + message, flush=True)


def token():
    status, page = request({'acao': 'formIncluir'})
    if status != 200:
        raise AssertionError('Formulário indisponível: ' + str(status))
    return re.search(r'name="csrf" value="([^"]+)"', page).group(1)


def post(action, **values):
    return request({'acao': action, 'csrf': token(), **values}, 'POST')


try:
    status, page = request({'acao': 'listar'})
    check(status == 200 and 'Produtos cadastrados' in page, 'Listagem com JSP e EJB')
    check('table-striped' in page and 'bootstrap@5.3.8' in page, 'Bootstrap na listagem')
    check('Preço de venda' in page and 'Ações' in page, 'Acentuação UTF-8 na listagem')
    status, page = request({'acao': 'formIncluir'})
    check(status == 200 and 'name="nome"' in page, 'Formulário de inclusão')
    check('Preço de venda' in page, 'Acentuação UTF-8 no formulário')
    name = 'Teste ' + uuid.uuid4().hex[:8] + ' <script>alert(1)</script>'
    first_token = token()
    params = {'acao': 'incluir', 'csrf': first_token, 'nome': name, 'quantidade': '7', 'precoVenda': '12.50'}
    status, page = request(params, 'POST')
    check(status == 200 and 'Produto incluído.' in page, 'Inclusão persistida')
    row = next(row for row in re.findall(r'<tr>(.*?)</tr>', page, re.S) if html.escape(name) in row)
    product_id = re.search(r'acao=formAlterar&amp;id=(\d+)', row).group(1)
    created.append(product_id)
    check(name not in page and html.escape(name) in page, 'Nome escapado contra XSS')
    check(request(params, 'POST')[0] == 403, 'Reenvio do mesmo formulário bloqueado')
    status, page = request({'acao': 'formAlterar', 'id': product_id})
    check(status == 200 and html.escape(name) in page, 'Formulário recupera a entidade')
    status, page = post('alterar', id=product_id, nome='Produto atualizado', quantidade='8', precoVenda='15,75')
    check(status == 200 and 'Produto atualizado.' in page, 'Alteração persistida e preço com vírgula')
    status, page = request({'acao': 'formAlterar', 'id': product_id})
    check('value="8"' in page and 'value="15.75"' in page, 'Nova consulta confirma os valores gravados')
    status, _ = request({'acao': 'excluir', 'id': product_id})
    check(status == 200 and request({'acao': 'formAlterar', 'id': product_id})[0] == 200,
          'GET de exclusão apenas pede confirmação')
    for field, value in [('nome', ''), ('nome', 'x' * 101), ('quantidade', '-1'),
                         ('quantidade', '1.5'), ('quantidade', '2147483648'),
                         ('precoVenda', '-1'), ('precoVenda', 'NaN'), ('precoVenda', 'Infinity')]:
        values = {'nome': 'Teste inválido', 'quantidade': '1', 'precoVenda': '1'}
        values[field] = value
        status, page = post('incluir', **values)
        check(status == 400 and 'alert-danger' in page, f'Validação: {field}={value[:20]}')
    check(request({'acao': 'incluir', 'nome': 'Sem token'}, 'POST')[0] == 403, 'POST sem token rejeitado')
    check(request({'acao': 'incluir'})[0] == 405, 'Inclusão por GET rejeitada')
    check(request({'acao': 'alterar'})[0] == 405, 'Alteração por GET rejeitada')
    check(request({'acao': 'desconhecida'})[0] == 400, 'Ação desconhecida rejeitada')
    check(request({'acao': 'formAlterar', 'id': 'abc'})[0] == 400, 'ID inválido rejeitado')
    check(request({'acao': 'formAlterar', 'id': '2147483647'})[0] == 404, 'Produto inexistente retorna 404')
    check(client.open(base + '/ServletProduto', timeout=30).status == 200, 'Servlet de teste funciona')
    status, page = post('excluir', id=product_id)
    check(status == 200 and 'Produto excluído.' in page, 'Exclusão persistida')
    check(request({'acao': 'formAlterar', 'id': product_id})[0] == 404, 'Consulta confirma a exclusão')
    created.remove(product_id)
    print(f'\n{checks} verificações HTTP passaram.')
finally:
    for product_id in created:
        post('excluir', id=product_id)
