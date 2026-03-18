import Cookies from 'js-cookie'

const TokenKey = 'Student-Token'

export function getToken(): string | undefined {
  return Cookies.get(TokenKey)
}

export function removeToken(): void {
  Cookies.remove(TokenKey)
}
