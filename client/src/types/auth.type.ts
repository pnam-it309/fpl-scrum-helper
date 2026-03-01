export interface DecodedToken {
  userId: string
  userCode: string
  fullName: string
  rolesName: string
  rolesCode: string[]
  exp: number
  email: string
  pictureUrl: string
  roleSwitch: string
  roleScreen: string | undefined
  idFacility: string | null
}

export interface UserInformation {
  userId: string | undefined
  userCode: string | undefined
  fullName: string | undefined
  rolesNames: string[] | string
  rolesCodes: string[] | string
  email: string | undefined
  pictureUrl: string | undefined
  roleSwitch: string | undefined
  roleScreen: string | undefined // role Lấy từ Fe khi login
  idFacility: string | null
}
