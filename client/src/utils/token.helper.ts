import type  { DecodedToken, UserInformation } from '@/types/auth.type'
import { jwtDecode } from 'jwt-decode'

export const getUserInformation = (token: string): UserInformation => {
  const decoded = jwtDecode<DecodedToken>(token)
  
  return {
    userId: decoded.userId,
    userCode: decoded.userCode,
    fullName: decoded.fullName,
    rolesNames: decoded.rolesName,
    rolesCodes: decoded.rolesCode,
    email: decoded.email,
    pictureUrl: decoded.pictureUrl,
    roleSwitch: decoded.roleSwitch,
    roleScreen:decoded.roleScreen,
    idFacility:decoded.idFacility,
  }
}

export const getRolesUser = (token: string): string[] => {
  const decoded = jwtDecode<DecodedToken>(token)
  return decoded.rolesCode
}

export const getExpireTime = (token: string): number => {
  const decoded = jwtDecode<DecodedToken>(token)
  return decoded.exp
}
