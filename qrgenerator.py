import qrcode


def main():
    ids = []
    with open("generated-orders.txt", "r") as order_ids:
        ids = order_ids.read().splitlines()

    for idx in ids:
        qr = qrcode.QRCode(version=1,
                           box_size=10,
                           border=5)
        qr.add_data(idx)
        qr.make()
        img = qr.make_image(fill='black', back_color='white')
        img.save(f"order qr codes\\{idx}.png")


if __name__ == '__main__':
    main()
